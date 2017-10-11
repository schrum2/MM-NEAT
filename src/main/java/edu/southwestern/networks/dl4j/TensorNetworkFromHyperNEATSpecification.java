package edu.southwestern.networks.dl4j;

import java.util.List;

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

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
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
                .updater(Updater.ADAM) // CHANGE THIS?
                //.iterations(iterations) // WHAT GOES HERE?
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .l1(1e-4) // KEEP?
                .regularization(true)
                .l2(5 * 1e-4); // KEEP?
                
        int kernel = Parameters.parameters.integerParameter("receptiveFieldSize");
        int[] kernelArray = new int[]{kernel, kernel};
        int stride = Parameters.parameters.integerParameter("stride");
        int[] strideArray = new int[]{stride, stride};
        boolean zeroPadding = Parameters.parameters.booleanParameter("zeroPadding");
        int[] zeroPaddingArray = zeroPadding ? new int[]{stride/2, stride/2} : new int[]{0,0};
        // Hidden layer construction will be based on these parameters instead of the actual substrate list
		int processWidth = Parameters.parameters.integerParameter("HNProcessWidth");
		int processDepth = Parameters.parameters.integerParameter("HNProcessDepth");
        
        ListBuilder listBuilder = builder.list(); // Building the DL4J network
        
        int hiddenLayer;
		for(hiddenLayer = 0; hiddenLayer < processDepth; hiddenLayer++) { // Add 2D hidden/processing layer(s)
			if(CommonConstants.convolution) {				
				ConvolutionLayer.Builder layer = new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray)
						.name("cnn"+(hiddenLayer+1))
						.convolutionMode(ConvolutionMode.Same); // What is this?

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
