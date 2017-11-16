package edu.southwestern.util.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import org.nd4j.linalg.ops.transforms.Transforms;
import org.nd4j.linalg.ops.transforms.Transforms.*;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.random.RandomNumbers;

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
	 * Image conversion/size properties
	 */
	public static final int HEIGHT = 224;
	public static final int WIDTH = 224;
	public static final int CHANNELS = 3;
		
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
		INDArray squares = Transforms.pow(diff, 2); // element-wise squaring
		return squares.sumNumber().doubleValue();
	}
	
	/**
	 * After passing in the content, style, and combination images,
	 * compute the loss with respect to the content. Based off of:
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * 
	 * @param activations Intermediate layer activations from the three inputs
	 * @return Weighted content loss component
	 */
	public static double content_loss(Map<String, INDArray> activations) {
		INDArray block2_conv2_features = activations.get("block2_conv2");
		INDArray content_features = block2_conv2_features.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
		INDArray combination_features = block2_conv2_features.get(NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
		
//		System.out.println(content_features.shapeInfoToString());
//		System.out.println(combination_features.shapeInfoToString());
		
		return content_weight * sumOfSquaredErrors(content_features, combination_features);
	}
	
	/**
	 * Computing the Gram matrix as described here:
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * Permuting dimensions is not needed because DL4J stores
	 * the channel at the front rather than the end of the tensor.
	 * Basically, each tensor is flattened into a vector so that
	 * the dot product can be calculated.
	 * 
	 * @param x Tensor to get Gram matrix of
	 * @return Resulting Gram matrix
	 */
	public static INDArray gram_matrix(INDArray x) {
		INDArray flattened = Nd4j.toFlattened(x);
		// mmul is dot product/outer product
		INDArray gram = flattened.mmul(flattened.dup().transpose()); // Is the dup necessary?
		return gram;
	}
	
	/**
	 * This method is simply called style_loss in
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * but it takes inputs for intermediate activations from a particular
	 * layer, hence my re-name. These values contribute to the total
	 * style loss.
	 * 
	 * @param style Activations from intermediate layer of CNN for style image input
	 * @param combination Activations from intermediate layer of CNN for combination image input
	 * @return Loss contribution from this comparison
	 */
	public static double style_loss_for_one_layer(INDArray style, INDArray combination) {
		INDArray s = gram_matrix(style);
		INDArray c = gram_matrix(combination);
		double size = HEIGHT*WIDTH;
		return sumOfSquaredErrors(s,c) / (4.0 * (CHANNELS * CHANNELS) * (size * size));
	}
	
	/**
	 * The overall style loss calculation shown in 
	 * https://harishnarayanan.org/writing/artistic-style-transfer/
	 * for every relevant intermediate layer of the CNN.
	 * 
	 * @param activations Intermediate activations of all CNN layers
	 * @return weighted style loss component
	 */
	public static double style_loss(Map<String, INDArray> activations) {
		String[] feature_layers = new String[] {
				"block1_conv2", "block2_conv2",
                "block3_conv3", "block4_conv3",
                "block5_conv3"};
		double loss = 0.0;
		for(String layer_name : feature_layers) {
			INDArray layer_features = activations.get(layer_name);
			INDArray style_features = layer_features.get(NDArrayIndex.point(1), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
			INDArray combination_features = layer_features.get(NDArrayIndex.point(2), NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.all());
			double sl = style_loss_for_one_layer(style_features, combination_features);
			loss += (style_weight / feature_layers.length) * sl;
		}
		return loss;
	}
	
	/**
	 * Returns weighted total variation loss that smooths the error across the image.
	 * Based on https://harishnarayanan.org/writing/artistic-style-transfer/ again.
	 * I'm not sure what the point of leaving out certain edges is in the tensor bounds,
	 * but I expect the point is to reduce the variance between directly adjacent pixels.
	 * 
	 * @param combination Combination image
	 * @return Weighted total variation loss across combo image
	 */
	public static double total_variation_loss(INDArray combination) {
		INDArray sliceA1 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT-1), NDArrayIndex.interval(0, WIDTH-1));
		INDArray sliceA2 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(1, HEIGHT), NDArrayIndex.interval(0, WIDTH-1));
		INDArray diffA = sliceA1.sub(sliceA2);
		INDArray a = Transforms.pow(diffA, 2); // element-wise squaring
		
		INDArray sliceB1 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT-1), NDArrayIndex.interval(0, WIDTH-1));
		INDArray sliceB2 = combination.get(NDArrayIndex.all(), NDArrayIndex.all(), NDArrayIndex.interval(0, HEIGHT-1), NDArrayIndex.interval(1, WIDTH));
		INDArray diffB = sliceB1.sub(sliceB2);
		INDArray b = Transforms.pow(diffB, 2); // element-wise squaring
		
		INDArray add = a.add(b);
		INDArray pow = Transforms.pow(add, 1.25);
		return total_variation_weight * pow.sumNumber().doubleValue();
	}
	
	/**
	 * Computes complete loss function for the neural style transfer
	 * optimization problem by adding up all of the components.
	 * 
	 * @param activations Activations from the content, style, and combination images (TODO: cache those that don't change)
	 * @param combination Combined image (so far)
	 * @return Loss value
	 */
	public static double neuralStyleLoss(Map<String, INDArray> activations, INDArray combination) {
		double loss = 0;
		loss += content_loss(activations);
		loss += style_loss(activations);
		loss += total_variation_loss(combination);
		return loss;
	}
	
	public static void main(String[] args) throws IOException {		
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

		int totalEntries = CHANNELS*HEIGHT*WIDTH;
		int[] upper = new int[totalEntries];
		Arrays.fill(upper, 256);
		INDArray combination = Nd4j.create(ArrayUtil.doubleArrayFromIntegerArray(RandomNumbers.randomIntArray(upper)), new int[] {1, CHANNELS, HEIGHT, WIDTH});
//		BufferedImage noise = GraphicsUtil.imageFromINDArray(combination);
//		DrawingPanel panel = GraphicsUtil.drawImage(noise, "Noise", WIDTH, HEIGHT); 
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		scaler.transform(combination);
		
		INDArray input = Nd4j.concat(0, content, style, combination);
		
		//System.out.println(input.shapeInfoToString());
		//System.out.println(vgg16.summary());
		
		INDArray[] result = vgg16.output(input);
		Map<String, INDArray> activations = vgg16.feedForward();
		
		double loss = neuralStyleLoss(activations, combination);
		
		
				
	}
}
