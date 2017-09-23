package edu.southwestern.networks.dl4j;

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
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.DropoutLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
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
                
                // EVERYTHING BELOW THIS POINT NEEDS TO BE CUSTOMIZED BASED ON HIDDEN SUBSTRATE LIST
        ListBuilder listBuilder = builder.list()
                .layer(0, new ConvolutionLayer.Builder(new int[]{4, 4}, new int[]{1, 1}, new int[]{0, 0}).name("cnn1").convolutionMode(ConvolutionMode.Same)
                    .nIn(3).nOut(64).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)//.learningRateDecayPolicy(LearningRatePolicy.Step)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(1, new ConvolutionLayer.Builder(new int[]{4,4}, new int[] {1,1}, new int[] {0,0}).name("cnn2").convolutionMode(ConvolutionMode.Same)
                    .nOut(64).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(2, new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,2}).name("maxpool2").build())

                .layer(3, new ConvolutionLayer.Builder(new int[]{4,4}, new int[] {1,1}, new int[] {0,0}).name("cnn3").convolutionMode(ConvolutionMode.Same)
                    .nOut(96).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(4, new ConvolutionLayer.Builder(new int[]{4,4}, new int[] {1,1}, new int[] {0,0}).name("cnn4").convolutionMode(ConvolutionMode.Same)
                    .nOut(96).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())

                .layer(5, new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {0,0}).name("cnn5").convolutionMode(ConvolutionMode.Same)
                    .nOut(128).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(6, new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {0,0}).name("cnn6").convolutionMode(ConvolutionMode.Same)
                    .nOut(128).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())

                .layer(7, new ConvolutionLayer.Builder(new int[]{2,2}, new int[] {1,1}, new int[] {0,0}).name("cnn7").convolutionMode(ConvolutionMode.Same)
                    .nOut(256).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(8, new ConvolutionLayer.Builder(new int[]{2,2}, new int[] {1,1}, new int[] {0,0}).name("cnn8").convolutionMode(ConvolutionMode.Same)
                    .nOut(256).weightInit(WeightInit.XAVIER_UNIFORM).activation(Activation.RELU)
                    .learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(9, new SubsamplingLayer.Builder(PoolingType.MAX, new int[]{2,2}).name("maxpool8").build())

                .layer(10, new DenseLayer.Builder().name("ffn1").nOut(1024).learningRate(1e-3).biasInit(1e-3).biasLearningRate(1e-3*2).build())
                .layer(11,new DropoutLayer.Builder().name("dropout1").dropOut(0.2).build())
                .layer(12, new DenseLayer.Builder().name("ffn2").nOut(1024).learningRate(1e-2).biasInit(1e-2).biasLearningRate(1e-2*2).build())
                .layer(13,new DropoutLayer.Builder().name("dropout2").dropOut(0.2).build())
                .layer(14, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                    .name("output")
                    .nOut(outputCount)
                    .activation(Activation.SOFTMAX)
                    .build());
                
                // ABOVE THIS IS THE END OF THE HIDDEN SUBSTRATE USE
                
        	MultiLayerConfiguration conf = listBuilder
    		    .backprop(true)
                .pretrain(false)
                .setInputType(InputType.convolutional(inputShape[DL4JNetworkWrapper.INDEX_INPUT_HEIGHT], 
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
