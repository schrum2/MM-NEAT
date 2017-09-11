package org.deeplearning4j.examples.vgg16;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.graphics.GraphicsUtil;

public class VGG16Test {


	public static void main(String[] args) throws IOException {
		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
		results(loader.asMatrix(new File("data/imagematch/car.jpg")));
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("data/imagematch/cat.jpg")));
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		// check output labels of result
//		String decodedLabels = labels.decodePredictions(output[0]);
//		System.out.println(decodedLabels);
	}
	
	public static void results(INDArray image) {
		Map<String,Float> results = GraphicsUtil.getImageNetPredictions(image, true);
		for(String label : results.keySet()) {
			System.out.println(label + ":" + results.get(label));
		}
	}
}
