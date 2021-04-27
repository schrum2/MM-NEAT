package edu.southwestern.networks.dl4j;

import java.util.HashMap;
import java.util.List;

import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;

public class TensorNetworkFromHyperNEATSpecification implements TensorNetwork {

	MultiLayerNetwork model;
	
	/**
	 * Default constructor gets the HyperNEATTask from the HyperNEATUtil, which
	 * comes from MMNEAT.
	 */
	public TensorNetworkFromHyperNEATSpecification() {
		this(HyperNEATUtil.getHyperNEATTask());
	}
	
	/**
	 * Construct randomized convolutional network based on the network specification
	 * for a given HyperNEAT task. Needs more generalization, but works for the
	 * restricted case of a network with only convolutional layers.
	 * @param hnt HyperNEATTask
	 */
	public TensorNetworkFromHyperNEATSpecification(HyperNEATTask hnt) {
        List<Substrate> substrates = hnt.getSubstrateInformation();

        int[] inputShape = HyperNEATUtil.getInputShape(substrates);
		int outputCount = HyperNEATUtil.getOutputCount(substrates);
		
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(Parameters.parameters.integerParameter("randomSeed"))
                .cacheMode(CacheMode.DEVICE) // KEEP THIS?
                //.biasLearningRate(0.02) // KEEP?
                .updater(new Nesterovs(Parameters.parameters.doubleParameter("backpropLearningRate"), 0.9))  //.updater(Updater.ADAM) // Use Nesterovs or ADAM or something else?
                //.iterations(1) // Causes error in new DL4J: 1.0.0-beta
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
                //.l1(1e-4) // KEEP?
                //.l2(5 * 1e-4) // KEEP?
                //.regularization(true) // Causes error in new DL4J: 1.0.0-beta
        		.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
                
        int xKernel = Parameters.parameters.integerParameter("receptiveFieldWidth");
        int yKernel = Parameters.parameters.integerParameter("receptiveFieldHeight");
        int[] kernelArray = new int[]{xKernel, yKernel};
        int stride = Parameters.parameters.integerParameter("stride");
        int[] strideArray = new int[]{stride, stride};
        boolean zeroPadding = Parameters.parameters.booleanParameter("zeroPadding");
        int[] zeroPaddingArray = new int[]{0,0}; // Is actually ignored if zeroPadding is used
        // Hidden layer construction will be based on these parameters instead of the actual substrate list
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
        
        ListBuilder listBuilder = builder.list(); // Building the DL4J network
        
        int hiddenLayer;
        int substrateIndex = 0;
        // Get first hidden processing substrate
        while(substrates.get(substrateIndex).getSubLocation().t2 == 0) {
        	substrateIndex++;
        }
        
		for(hiddenLayer = 0; hiddenLayer < processDepth; hiddenLayer++) { // Add 2D hidden/processing layer(s)
			// Assumes all substrates in same layer use same activation function ftype
			int ftype = substrates.get(substrateIndex).getFtype();
//			System.out.println(hiddenLayer + ":" + substrateIndex + ":" + ftype);
			int currentLayer = substrates.get(substrateIndex).getSubLocation().t2;
			// Move substrateIndex to next layer
	        while(substrates.get(substrateIndex).getSubLocation().t2 == currentLayer) {
	        	substrateIndex++;
	        }
			
			if(CommonConstants.convolution) {				
				ConvolutionLayer.Builder layer = new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
						.name("cnn"+(hiddenLayer+1))
						.convolutionMode(zeroPadding ? 
								ConvolutionMode.Same : // Each layer is same size, but padded with zeros
								ConvolutionMode.Strict); // Higher layers shrink based on padding and stride

				// Only first layer needs to specify input channels, because for the rest, the
				// outputs from the previous layer determine the inputs to the next
				if(hiddenLayer == 0) {
					layer = layer.nIn(inputShape[DL4JNetworkWrapper.INDEX_INPUT_CHANNELS]);
				}
				
				layer = layer
						.nOut(processWidth)
						.weightInit(WeightInit.XAVIER_UNIFORM) // Keep this?
						.activation(ActivationFunctions.getDL4JEquivalent(ftype))
						//.learningRateDecayPolicy(LearningRatePolicy.Step) // KEEP?
						//.learningRate(1e-2) // KEEP? 
						.biasInit(1e-2) // Change?
						//.biasLearningRate(1e-2*2); // Causses error in new DL4J: 1.0.0-beta
						.updater(new Adam(1e-2*2)); // Replaced biasLearningRate above
						
				listBuilder = listBuilder.layer(hiddenLayer, layer.build());
			} else {
				throw new UnsupportedOperationException("Can only use DL4J to set up convolutional networks for now");
			}

			// Keep any MAXPOOL layers?
			//listBuilder = listBuilder
			//		.layer(2, new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,2}).name("maxpool2").build());

			// Have any Fully Connected layers?
			//listBuilder = listBuilder
			//		.layer(10, new DenseLayer.Builder().name("ffn1").nOut(1024).learningRate(1e-3).biasInit(1e-3).biasLearningRate(1e-3*2).build());

			// Have any drop-out layers?
			//listBuilder = listBuilder
			//		.layer(11,new DropoutLayer.Builder().name("dropout1").dropOut(0.2).build());
		}

		// substrateIndex should be first output substrate
		int outputFtype = substrates.get(substrateIndex).getFtype();
//		System.out.println("output:" + substrateIndex + ":" + outputFtype);

		listBuilder = listBuilder
				.layer(hiddenLayer, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
						.name("output")
						.nOut(outputCount)
						.activation(ActivationFunctions.getDL4JEquivalent(outputFtype))
						.build());

		MultiLayerConfiguration conf = listBuilder
				// Removed in the upgrade to DL4J beta7. Do we not need this anymore?
				//.backprop(true)
				//.pretrain(false)
				.setInputType(InputType.convolutional(
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_HEIGHT], 
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_WIDTH], 
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_CHANNELS]))
				.build();

		model = new MultiLayerNetwork(conf);
		model.init();        
	}

	/**
	 * The HyperNEATCPPNGenotype can be used to create a large substrate network TWEANNGenotype
	 * that specifies all of the weights in the network directly. If both the TWEANNGenotype and
	 * this TensorNetworkFromHyperNEATSpecification instance were created from the same HyperNEATTask
	 * specification, then the model weights in this instance can be filled in by the provided
	 * TWEANNGenotype.
	 * 
	 * @param tg genotype that directly encodes the substrate network created by a CPPN.
	 */
	public void fillWeightsFromHyperNEATNetwork(HyperNEATTask hnt, TWEANNGenotype tg) {

		// Might add support for this later
		if(!Parameters.parameters.booleanParameter("convolutionWeightSharing")) {
			throw new UnsupportedOperationException("Currently, convolutionWeightSharing must be used with DL4J networks");
		}
		// Can this be supported?
		if(Parameters.parameters.booleanParameter("extraHNLinks")) {
			throw new UnsupportedOperationException("Links from input layer directly to the output layer are not allowed in DL4J networks");
		}
		
        List<Substrate> substrates = hnt.getSubstrateInformation();
        
        List<SubstrateConnectivity> substrateConnectivity = hnt.getSubstrateConnectivity();
        // Quick look-up for whether layers are joined in a convolutional manner
        HashMap<String, Integer> areConnectionsConvolutional = new HashMap<>();
        for(SubstrateConnectivity trip : substrateConnectivity) {
        	areConnectionsConvolutional.put(trip.sourceSubstrateName + "_" + trip.targetSubstrateName, trip.connectivityType);
        }
        
        int xKernel = Parameters.parameters.integerParameter("receptiveFieldWidth");
        int yKernel = Parameters.parameters.integerParameter("receptiveFieldHeight");
		
		// This method heavily relies on the fact that node innovation numbers
		// in a substrate network's genotype start at 0 and are sequentially numbered
		// while going through the different substrates at each layer.
		
		Layer[] layers = model.getLayers();
		
		// Get number of input substrates
		int substratesInSourceLayer = 0;
		int firstSourceSubstrateIndex = 0; 
		int sourceLayerDepth = 0;
		long layerStartInnovation = 0;
		for(int i = 0; substrates.get(i).getSubLocation().t2 == 0; i++) {
			substratesInSourceLayer++;
		}
		
		for(Layer layer : layers) { // Go through each layer
			//System.out.println("BEFORE\n" + layer.paramTable());
			
			Substrate firstSourceSubstrate = substrates.get(firstSourceSubstrateIndex);
			// Assumes all substrates as same level have same size
			int sourceSubstrateWidth = firstSourceSubstrate.getSize().t1; 
			long nextLayerStartInnovation = layerStartInnovation + (substratesInSourceLayer * firstSourceSubstrate.numberOfNeurons()); 
			
			// Count substrates in target layer
			int targetLayerDepth = sourceLayerDepth + 1;
			int substratesInTargetLayer = 0; 
			int firstTargetSubstrateIndex = firstSourceSubstrateIndex+substratesInSourceLayer;
			for(int i = firstTargetSubstrateIndex; i < substrates.size() && substrates.get(i).getSubLocation().t2 == targetLayerDepth; i++) {
				substratesInTargetLayer++;
			}
			Substrate firstTargetSubstrate = substrates.get(firstTargetSubstrateIndex); 
			
			// If substrate connections are convolutional, then assume all layer connections are
			if(SubstrateConnectivity.CTYPE_CONVOLUTION == (areConnectionsConvolutional.get(firstSourceSubstrate.getName() + "_" + firstTargetSubstrate.getName()))) {				
				INDArray weights = layer.getParam("W"); // Convolutional weights
				INDArray biases = layer.getParam("b"); // Biases: one per target substrate
				
				int targetNeuronsToSkip = 0;
				// Change weights: Loop through every section of parameter INDArray
				for(int targetChannel = 0; targetChannel < substratesInTargetLayer; targetChannel++) {
					// Get bias of first neuron in substrate, and assume weight/bias sharing
					long targetInnovation = nextLayerStartInnovation + targetNeuronsToSkip;
					// In substrate networks, innovation number matches node list index
					double newBias = tg.nodes.get((int) targetInnovation).getBias();
					// Set bias value in DL4J layer
					biases.putScalar(targetChannel, newBias);
					int sourceNeuronsToSkip = 0; // Reset for each target substrate
					for(int sourceChannel = 0; sourceChannel < substratesInSourceLayer; sourceChannel++) {						
						for(int height = 0; height < yKernel; height++) {
							for(int width = 0; width < xKernel; width++) {
								// Figure out source neuron in TWEANNGenotype, and rely on weight sharing
								long sourceInnovation = layerStartInnovation + sourceNeuronsToSkip + (height*sourceSubstrateWidth) + width;
								// Get weight from TWEANNGenotype
								LinkGene lg = tg.getLinkBetween(sourceInnovation, targetInnovation);
								assert CommonConstants.linkExpressionThreshold > 0 || lg != null : "No link between " + sourceInnovation + " and " + targetInnovation;
								double newWeight = lg == null ? 0 : lg.weight; // Null link indicates a weight of 0.0
								// Replace value in INDArray
								weights.putScalar(targetChannel, sourceChannel, height, width, newWeight);
							}	
						}
						// Go past all neurons in given substrate within layer
						sourceNeuronsToSkip += substrates.get(firstSourceSubstrateIndex + sourceChannel).numberOfNeurons();
					}
					// Go past all neurons in given substrate within layer
					targetNeuronsToSkip += substrates.get(firstTargetSubstrateIndex + targetChannel).numberOfNeurons();
				}
				// Set new weights
				layer.setParam("W", weights);
				// Set new biases
				layer.setParam("b", biases);
				
			} else { // Assume fully connected
				INDArray weights = layer.getParam("W"); // Weights
				int paramPosition = 0; // Position in linear weights array
				INDArray biases = layer.getParam("b"); // Biases
				
				int targetNeuronsToSkip = 0;
				// Change weights: Loop through every section of parameter INDArray
				for(int targetSubstrate = 0; targetSubstrate < substratesInTargetLayer; targetSubstrate++) {
					Substrate target = substrates.get(firstTargetSubstrateIndex + targetSubstrate);
					int targetNeuronsInSubstrate = target.numberOfNeurons();
					int sourceNeuronsToSkip = 0; // Reset for each target substrate
					for(int sourceSubstrate = 0; sourceSubstrate < substratesInSourceLayer; sourceSubstrate++) {
						Substrate source = substrates.get(firstSourceSubstrateIndex + sourceSubstrate);
						int sourceNeuronsInSubstrate = source.numberOfNeurons();
						for(int targetNeuron = 0; targetNeuron < targetNeuronsInSubstrate; targetNeuron++) {
							// Get bias of neuron in substrate
							long targetInnovation = nextLayerStartInnovation + targetNeuronsToSkip + targetNeuron;
							// In substrate networks, innovation number matches node list index
							double newBias = tg.nodes.get((int) targetInnovation).getBias();
							// Set bias value in DL4J layer
							biases.putScalar(targetNeuronsToSkip + targetNeuron, newBias);

							for(int sourceNeuron = 0; sourceNeuron < sourceNeuronsInSubstrate; sourceNeuron++) {
								// Figure out source neuron in TWEANNGenotype, and rely on weight sharing
								long sourceInnovation = layerStartInnovation + sourceNeuronsToSkip + sourceNeuron;
								// Get weight from TWEANNGenotype
								LinkGene lg = tg.getLinkBetween(sourceInnovation, targetInnovation);
								assert CommonConstants.linkExpressionThreshold > 0 || lg != null : "No link between " + sourceInnovation + " and " + targetInnovation;
								double newWeight = lg == null ? 0 : lg.weight; // Null link indicates a weight of 0.0
								// Replace value in INDArray
								weights.putScalar(paramPosition++, newWeight);
							}
						}						
						// Go past all neurons in given substrate within layer
						sourceNeuronsToSkip += sourceNeuronsInSubstrate;
					}
					// Go past all neurons in given substrate within layer
					targetNeuronsToSkip += targetNeuronsInSubstrate;
				}
				// Set new weights
				layer.setParam("W", weights);
				// Set new biases
				layer.setParam("b", biases);
			}
			
			// Update source values with target values for next iteration up the network
			substratesInSourceLayer = substratesInTargetLayer;
			firstSourceSubstrateIndex = firstTargetSubstrateIndex; 
			sourceLayerDepth = targetLayerDepth;
			layerStartInnovation = nextLayerStartInnovation;
			//System.out.println("AFTER\n" + layer.paramTable());
		}
	}
	
	
	@Override
	public INDArray output(INDArray input) {
		return model.output(input);
	}

	@Override
	public void flush() {
		model.rnnClearPreviousState();
	}

	@Override
	public void fit(INDArray input, INDArray targets) {
		model.fit(input, targets);
	}

	@Override
	public void fit(DataSet minibatch) {
		model.fit(minibatch);
	}
	
	/**
	 * This whole method was just used for troubleshooting.
	 * @param args
	 */
	public static void main(String[] args) {
		// Minimal example to test input shape issues
		Parameters.initializeParameterCollections(new String[] {"runNumber:0","randomSeed:0","io:false","netio:false","maxGens:10","watch:false",
				"task:edu.southwestern.tasks.rlglue.tetris.HyperNEATTetrisTask",
				"rlGlueEnvironment:org.rlcommunity.environments.tetris.Tetris",
				"rlGlueExtractor:edu.southwestern.tasks.rlglue.featureextractors.tetris.RawTetrisStateExtractor",
				"rlGlueAgent:edu.southwestern.tasks.rlglue.tetris.TetrisAfterStateAgent",
				"splitRawTetrisInputs:true",
				"senseHolesDifferently:true",
				"hyperNEAT:true", // Prevents extra bias input
				"steps:500000",
				"trials:1000", // Lots of trials so same network keeps learning
				"linkExpressionThreshold:-0.1", // Express all links
				"heterogeneousSubstrateActivations:true", // Allow mix of activation functions
				"inputsUseID:true", // Inputs are Identity (mandatory in DL4J?)
				"stride:1","receptiveFieldHeight:3","receptiveFieldWidth:3","zeroPadding:false","convolutionWeightSharing:true",
				"HNProcessDepth:4","HNProcessWidth:4","convolution:true",
				"experiment:edu.southwestern.experiment.rl.EvaluateDL4JNetworkExperiment"});
		MMNEAT.loadClasses();
		

		// This network is too idealized. Need to get the real deal to cause the error/crash
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .miniBatch(true) 
                .cacheMode(CacheMode.DEVICE) 
                //.learningRate(.01) // Error in 1.0.0-beta: Moved into Nesterovs below
                .updater(new Nesterovs(.01, 0.9)) 
                //.iterations(1) // Error in 1.0.0-beta
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) 
                //.regularization(true); // Error in 1.0.0-beta
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
                
        int[] kernelArray = new int[]{3,3};
        int[] strideArray = new int[]{1,1};
        int[] zeroPaddingArray = new int[]{0,0}; 
		int processWidth = 4;
        
        ListBuilder listBuilder = builder.list(); // Building the DL4J network

        listBuilder = listBuilder.layer(0, new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
        		.name("cnn1")
        		.convolutionMode(ConvolutionMode.Strict)
        		.nIn(2) // 2 input channels
        		.nOut(processWidth)
        		.weightInit(WeightInit.XAVIER_UNIFORM)
        		.activation(Activation.RELU)
        		//.learningRate(1e-2) // Error in 1.0.0-beta: Moved to Adam below
        		.updater(new Adam(1e-2))
        		.biasInit(1e-2) 
        		//.biasLearningRate(1e-2*2) // Error in 1.0.0-beta
        		.build());

        listBuilder = listBuilder.layer(1, new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
        		.name("cnn2")
        		.convolutionMode(ConvolutionMode.Strict)
        		.nOut(processWidth)
        		.weightInit(WeightInit.XAVIER_UNIFORM)
        		.activation(Activation.RELU)
        		//.learningRate(1e-2) // Error in 1.0.0-beta: Moved to Adam below
        		.updater(new Adam(1e-2))
        		.biasInit(1e-2) 
        		//.biasLearningRate(1e-2*2) // Error in 1.0.0-beta
        		.build());

        listBuilder = listBuilder.layer(2, new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
        		.name("cnn3")
        		.convolutionMode(ConvolutionMode.Strict)
        		.nOut(processWidth)
        		.weightInit(WeightInit.XAVIER_UNIFORM)
        		.activation(Activation.RELU)
        		//.learningRate(1e-2) // Error in 1.0.0-beta: Moved to Adam below
        		.updater(new Adam(1e-2))
        		.biasInit(1e-2) 
        		//.biasLearningRate(1e-2*2) // Error in 1.0.0-beta
        		.build());

        listBuilder = listBuilder.layer(3, new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
        		.name("cnn4")
        		.convolutionMode(ConvolutionMode.Strict)
        		.nOut(processWidth)
        		.weightInit(WeightInit.XAVIER_UNIFORM)
        		.activation(Activation.RELU)
        		//.learningRate(1e-2) // Error in 1.0.0-beta: Moved to Adam below
        		.updater(new Adam(1e-2))
        		.biasInit(1e-2) 
        		//.biasLearningRate(1e-2*2) // Error in 1.0.0-beta
        		.build());

		listBuilder = listBuilder
				.layer(4, new OutputLayer.Builder(LossFunctions.LossFunction.MSE) 
						.name("output")
						.nOut(1)
						.activation(Activation.TANH)
						.build());

		MultiLayerConfiguration conf = listBuilder
				// Removed in the upgrade to DL4J beta7. Do we not need this anymore?
				//.backprop(true)
				//.pretrain(false)
				.setInputType(InputType.convolutional(20, 10, 2))
				.build();

		// For some reason, this model works
		MultiLayerNetwork niceModel = new MultiLayerNetwork(conf);
		niceModel.init();
		
		
		MultiLayerNetwork model = new TensorNetworkFromHyperNEATSpecification().model;
        model.init();      

        System.out.println("NICE MODEL");
        System.out.println(niceModel.summary());
        System.out.println(niceModel.getLayerWiseConfigurations());

        
        System.out.println("BAD MODEL");
        System.out.println(model.summary());
        System.out.println(model.getLayerWiseConfigurations());
        
        double[] linearInputs = new double[400];
        int[] inputShape = new int[] {1, 2, 20, 10}; // 2 separate 20 by 10 inputs
        
        INDArray input = Nd4j.create(linearInputs, inputShape, 'c');
        System.out.println(input.shapeInfoToString());
        
        INDArray niceOutput = niceModel.output(input, false); 
        // Try to fit it to its own output
        niceModel.fit(input, niceOutput);
        
        
        INDArray output = model.output(input, false); 
        System.out.println(output.shapeInfoToString());
        
        // Try to fit it to its own output
        model.fit(input, output);
	}
}
