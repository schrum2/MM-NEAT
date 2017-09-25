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
import edu.southwestern.parameters.Parameters;

public class TensorNetworkFromHyperNEATSpecification implements TensorNetwork {

	MultiLayerNetwork model;
	
	public TensorNetworkFromHyperNEATSpecification(HyperNEATTask hnt) {
		int[] inputShape = HyperNEATUtil.getInputShape(hnt.getSubstrateInformation());
		int outputCount = HyperNEATUtil.getOutputCount(hnt.getSubstrateInformation());
		
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
        
        ListBuilder listBuilder = builder.list(); // Building the DL4J network
        
        List<Substrate> substrates = hnt.getSubstrateInformation();
        int hiddenLayer = 0;
        for(Substrate sub : substrates) {
        	// Probably not right: It's not every substrate, but rather every group at the same depth
        	if(sub.getStype() == Substrate.PROCCESS_SUBSTRATE) {
                listBuilder = listBuilder
                        .layer(hiddenLayer++, new ConvolutionLayer.Builder(kernelArray, strideArray, zeroPaddingArray).name("cnn"+hiddenLayer)
                        .convolutionMode(ConvolutionMode.Same) // What is this?
                        .nIn(3) // ONLY THE FIRST CONV LAYER HAS THIS? IS THIS CHANNELS?
                        .nOut(64) // WHAT SHOULD THIS BE? IS THIS NUMBER OF FEATURES (COLUMN DEPTH)?
                        .weightInit(WeightInit.XAVIER_UNIFORM) // Keep this?
                        .activation(Activation.RELU)//.learningRateDecayPolicy(LearningRatePolicy.Step)
                        .learningRate(1e-2) // Change?
                        .biasInit(1e-2) // Change?
                        .biasLearningRate(1e-2*2) // Change?
                        .build());

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
        }

        listBuilder = listBuilder
        		.layer(hiddenLayer, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) // Use this Loss function?
        				.name("output")
        				.nOut(outputCount)
        				.activation(Activation.SOFTMAX) // CHANGE?
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
