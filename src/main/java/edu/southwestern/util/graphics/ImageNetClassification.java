package edu.southwestern.util.graphics;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

public class ImageNetClassification {
	public static final int NUM_IMAGE_NET_CLASSES = 1000;
	
	// Do not take the time to initialize this if not needed
	private static ComputationGraph imageNet = null;
	private static ImageNetLabels imageNetLabels = null;
	/**
	 * Initialize the ImageNet if it hasn't been done yet. This is only done
	 * once because the net weights should never change. Saving the result allows
	 * it to be re-used without re-initialization
	 */
	public static void initImageNet() {
		@SuppressWarnings("rawtypes")
		ZooModel model = new VGG16();
		try {
			imageNet = (ComputationGraph) model.initPretrained(PretrainedType.IMAGENET);
		} catch (IOException e) {
			System.out.println("Could not initialize ImageNet!");
			e.printStackTrace();
			System.exit(1);
		}
		// If image net is being used, then the labels will be needed as well
		imageNetLabels = new ImageNetLabels();
	}
	
	/**
	 * Takes an image represented within an INDArray and returns all of the scores
	 * that ImageNet assigns for each of its 1000 labels. Image Net may need to be
	 * initialized first.
	 * 
	 * @param image Image is a 2D matrix within DL4J's INDArray class
	 * @param preprocess Whether the image/matrix needs to be scaled to the appropriate size for ImageNet
	 * @return map of label to score for each of 1000 labels
	 */
	public static Map<String, Float> getImageNetLabelledPredictions(INDArray image, boolean preprocess) {		
		INDArray currentBatch = getImageNetPredictions(image, preprocess);
		return getImageNetLabelledPredictions(currentBatch);
	}
	
	/**
	 * Take an INDArray already that has already been processes by ImageNet (the output scores)
	 * and assign the ImageNet labels to them.
	 * @param precomputerScores Output of getImageNetPredictions
	 * @return Map of ImageNet labels to scores
	 */
	public static Map<String, Float> getImageNetLabelledPredictions(INDArray precomputerScores) {
		Map<String, Float> result = new HashMap<>();		
		for (int i = 0; i < NUM_IMAGE_NET_CLASSES; i++) {
			//System.out.println(labels.getLabel(i) + ": "+(currentBatch.getFloat(0,i)*100) + "%");
			result.put(imageNetLabels.getLabel(i), precomputerScores.getFloat(0,i));
		}
		return result;
	}
	
	/**
	 * Get raw ImageNet prediction scores from ImageNet without any labels.
	 * @param image Image is a 2N matrix within DL4J's INDArray class
	 * @param preprocess Whether the image/matrix needs to be scaled to the appropriate size for ImageNet
	 * @return INDArray of prediction scores for ImageNet's categories/labels
	 */
	public static INDArray getImageNetPredictions(INDArray image, boolean preprocess) {
		if(imageNet == null) initImageNet();
		if(preprocess) {
			DataNormalization scaler = new VGG16ImagePreProcessor();
			scaler.transform(image);
		}		
		INDArray[] output = imageNet.output(false, image);
		INDArray predictions = output[0];
		return predictions.getRow(0).dup(); // Should I duplicate with dup? Worth the load? Needed?
	}
}
