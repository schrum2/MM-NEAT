package edu.southwestern.util.graphics;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

/**
 * My attempt to implement the neural style transfer algorithm in DL4J:
 * https://arxiv.org/pdf/1508.06576.pdf
 * https://arxiv.org/pdf/1603.08155.pdf
 * https://harishnarayanan.org/writing/artistic-style-transfer/
 * 
 * @author Jacob Schrum
 */
public class NeuralStyleTransfer {

	/**
	 * Values suggested by
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * 
	 * Will likely change this, or make them parameters.
	 */
	public static double content_weight = 0.025;
	public static double style_weight = 5.0;
	public static double total_variation_weight = 1.0;
	
	/**
	 * Element-wise differences are squared, and then summed.
	 * This is modelled after the content_loss method defined in
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * 
	 * @param a One tensor
	 * @param b Another tensor
	 * @return Sum of squared errors: scalar
	 */
	public static double sumOfSquaredErrors(INDArray a, INDArray b) {
		INDArray diff = a.sub(b); // difference
		INDArray squares = diff.mul(diff); // element-wise squaring
		return squares.sumNumber().doubleValue();
	}
	
	public static double content_loss(Map<String, INDArray> activations) {
		INDArray block2_conv2_features = activations.get("block2_conv2");
		INDArray content_features = block2_conv2_features.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
		INDArray combination_features = block2_conv2_features.get(NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
		
		System.out.println(content_features.shapeInfoToString());
		System.out.println(combination_features.shapeInfoToString());
		
		return content_weight * sumOfSquaredErrors(content_features, combination_features);
	}
	
	public static void main(String[] args) throws IOException {
		
		final int HEIGHT = 224;
		final int WIDTH = 224;
		final int CHANNELS = 3;
		
		ZooModel zooModel = new VGG16();
		
		// Do I need separate copies for each image input?
		ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
	
		NativeImageLoader loader = new NativeImageLoader(HEIGHT, WIDTH, CHANNELS);
		DataNormalization scaler = new VGG16ImagePreProcessor();
		
		String contentFile = "data/imagematch/cat.jpg";
		INDArray content = loader.asMatrix(new File(contentFile));
		scaler.transform(content);
		
		String styleFile = "data/imagematch/supercreepypersonimage.jpg";
		INDArray style = loader.asMatrix(new File(styleFile));
		scaler.transform(style);

		INDArray combination = Nd4j.create(1, CHANNELS, HEIGHT, WIDTH);
		
		INDArray input = Nd4j.concat(0, content, style, combination);
		
		//System.out.println(input.shapeInfoToString());
		//System.out.println(vgg16.summary());
		
		INDArray[] result = vgg16.output(input);
		Map<String, INDArray> activations = vgg16.feedForward();
		
		System.out.println(content_loss(activations));
		
		
//		INDArray block1_conv2_features = activations.get("block1_conv2");		
//		INDArray block2_conv2_features = activations.get("block2_conv2");
//		INDArray block3_conv3_features = activations.get("block3_conv3");
//		INDArray block4_conv3_features = activations.get("block4_conv3");
//		INDArray block5_conv3_features = activations.get("block5_conv3");
				
	}
}
