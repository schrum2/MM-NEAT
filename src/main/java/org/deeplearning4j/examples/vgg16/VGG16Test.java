package org.deeplearning4j.examples.vgg16;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.graphics.ImageNetClassification;

public class VGG16Test {


	public static void main(String[] args) throws IOException {
		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
		results(loader.asMatrix(new File("data/imagematch/car.jpg")));
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("data/imagematch/cat.jpg")));
		MiscUtil.waitForReadStringAndEnterKeyPress();		
	}
	
	public static void results(INDArray image) {
		INDArray scores = ImageNetClassification.getImageNetPredictions(image, true);
		Map<String,Float> results = ImageNetClassification.getImageNetLabelledPredictions(scores);
		for(String label : results.keySet()) {
			System.out.println(label + ":" + results.get(label));
		}
		System.out.println("BEST: " + ImageNetClassification.bestLabel(scores));
		// check output labels of result
		String decodedLabels = new ImageNetLabels().decodePredictions(scores);
		System.out.println(decodedLabels);
	}
}
