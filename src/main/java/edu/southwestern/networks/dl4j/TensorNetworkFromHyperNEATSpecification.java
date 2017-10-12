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
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.southwestern.evolution.genotypes.HyperNEATCPPNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Triple;
import wox.serial.Easy;

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
                .updater(Updater.ADAM) // CHANGE THIS?
                //.iterations(iterations) // WHAT GOES HERE?
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .l1(1e-4) // KEEP?
                .regularization(true) // KEEP?
                .l2(5 * 1e-4); // KEEP?
                
        int kernel = Parameters.parameters.integerParameter("receptiveFieldSize");
        int[] kernelArray = new int[]{kernel, kernel};
        int stride = Parameters.parameters.integerParameter("stride");
        int[] strideArray = new int[]{stride, stride};
        boolean zeroPadding = Parameters.parameters.booleanParameter("zeroPadding");
        int[] zeroPaddingArray = new int[]{0,0}; // Is actually ignored if zeroPadding is used
        // Hidden layer construction will be based on these parameters instead of the actual substrate list
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
        
        ListBuilder listBuilder = builder.list(); // Building the DL4J network
        
        int hiddenLayer;
		for(hiddenLayer = 0; hiddenLayer < processDepth; hiddenLayer++) { // Add 2D hidden/processing layer(s)
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
						.activation(Activation.RELU)//.learningRateDecayPolicy(LearningRatePolicy.Step)
						.learningRate(1e-2) // Change?
						.biasInit(1e-2) // Change?
						.biasLearningRate(1e-2*2); // Change?

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

		listBuilder = listBuilder
				.layer(hiddenLayer, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) // Use this Loss function?
						.name("output")
						.nOut(outputCount)
						.activation(Activation.TANH) // Link DL4J activation functions to mine?
						.build());

		MultiLayerConfiguration conf = listBuilder
				.backprop(true)
				.pretrain(false)
				.setInputType(InputType.convolutional(
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_HEIGHT], 
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_WIDTH], 
						inputShape[DL4JNetworkWrapper.INDEX_INPUT_CHANNELS]))
				.build();

        model = new MultiLayerNetwork(conf);
        model.init();
        
        // BELOW IS EXPERIMENTAL
        HyperNEATCPPNGenotype cppn = new HyperNEATCPPNGenotype();
        fillWeightsFromHyperNEATNetwork(hnt, cppn.getSubstrateGenotype(hnt));        
        
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
		
		Easy.save(tg, "TEMP.xml");
		
        List<Substrate> substrates = hnt.getSubstrateInformation();
        
        List<Triple<String, String, Boolean>> substrateConnectivity = hnt.getSubstrateConnectivity();
        // Quick look-up for whether layers are joined in a convolutional manner
        HashMap<String, Boolean> areConnectionsConvolutional = new HashMap<>();
        for(Triple<String, String, Boolean> trip : substrateConnectivity) {
        	areConnectionsConvolutional.put(trip.t1 + "_" + trip.t2, trip.t3);
        }
        
        int kernel = Parameters.parameters.integerParameter("receptiveFieldSize");
		
		// This method heavily relies on the fact that node innovation numbers
		// in a substrate network's genotype start at 0 and are sequentially numbered
		// while going through the different substrates at each layer.
		
//		System.out.println("model.numParams(): " + model.numParams());
//		System.out.println("model.getnLayers(): " + model.getnLayers());
//		System.out.println("model.getLayerNames(): " + model.getLayerNames());
//		System.out.println(model.summary());
		
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
			
//			System.out.println("BEFORE\n" + layer.paramTable());
			
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
			Substrate firstTargetSubstrate = substrates.get(firstTargetSubstrateIndex); // Last substrate visited by previous loop
			
			// If substrate connections are convolutional, then assume all layer connections are
			if(areConnectionsConvolutional.get(firstSourceSubstrate.getName() + "_" + firstTargetSubstrate.getName())) {
//				System.out.println("substratesInTargetLayer: " + substratesInTargetLayer);
//				System.out.println("substratesInSourceLayer: " + substratesInSourceLayer);
				
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
//					System.out.println("targetInnovation: " + targetInnovation);
					int sourceNeuronsToSkip = 0; // Reset for each target substrate
					for(int sourceChannel = 0; sourceChannel < substratesInSourceLayer; sourceChannel++) {
//						System.out.println("\tsourceNeuronsToSkip: " + sourceNeuronsToSkip);
						
						for(int height = 0; height < kernel; height++) {
							for(int width = 0; width < kernel; width++) {
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
				
				// TODO
			}
			
			// Update source values with target values for next iteration up the network
			substratesInSourceLayer = substratesInTargetLayer;
			firstSourceSubstrateIndex = firstTargetSubstrateIndex; 
			sourceLayerDepth = targetLayerDepth;
			layerStartInnovation = nextLayerStartInnovation;
			
			System.out.println("AFTER\n" + layer.paramTable());
		}
        MiscUtil.waitForReadStringAndEnterKeyPress();

	}
	
	
	@Override
	public INDArray output(INDArray input) {
		return model.output(input);
	}

	@Override
	public void flush() {
		model.rnnClearPreviousState();
	}

}
