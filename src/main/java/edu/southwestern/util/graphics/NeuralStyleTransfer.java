package edu.southwestern.util.graphics;

import java.io.File;
import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

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
	public static Number sumOfSquaredErrors(INDArray a, INDArray b) {
		INDArray diff = a.sub(b); // difference
		INDArray squares = diff.mul(diff); // element-wise squaring
		return squares.sumNumber();
	}
	
	public static double content_loss(ComputationGraph vgg16) {
		// TODO
		return Double.NaN;
	}
	
	public static void main(String[] args) throws IOException {
		ZooModel zooModel = new VGG16();
		
		// Do I need separate copies for each image input?
		ComputationGraph vgg16 = (ComputationGraph) zooModel.initPretrained(PretrainedType.IMAGENET);
	
		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
		DataNormalization scaler = new VGG16ImagePreProcessor();
		
		String contentFile = "data/imagematch/cat.jpg";
		INDArray content = loader.asMatrix(new File(contentFile));
		scaler.transform(content);
		
		String styleFile = "data/imagematch/supercreepypersonimage.jpg";
		INDArray style = loader.asMatrix(new File(styleFile));
		scaler.transform(style);
		
		System.out.println(vgg16.summary());
		
		// Is calling activate again necessary? Can't the values just be retrieved?
		// Does this mean an input presentation has to occur first?
		INDArray block1_conv2_features = vgg16.getLayer("block1_conv2").activate();
		INDArray block2_conv2_features = vgg16.getLayer("block2_conv2").activate();
		INDArray block3_conv3_features = vgg16.getLayer("block3_conv3").activate();
		INDArray block4_conv3_features = vgg16.getLayer("block4_conv3").activate();
		INDArray block5_conv3_features = vgg16.getLayer("block5_conv3").activate();
				
	}
}
