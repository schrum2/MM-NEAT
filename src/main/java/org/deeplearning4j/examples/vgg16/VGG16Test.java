package org.deeplearning4j.examples.vgg16;

import java.io.File;
import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.VGG16;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.VGG16ImagePreProcessor;

public class VGG16Test {

	
	public static void main(String[] args) throws IOException {
        // set up model
        ZooModel model = new VGG16();
        ComputationGraph initializedModel = (ComputationGraph) model.initPretrained(PretrainedType.IMAGENET);

        // set up input and feedforward
//        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
//        INDArray image = loader.asMatrix("cat.jpg");
//        DataNormalization scaler = new VGG16ImagePreProcessor();
//        scaler.transform(image);
//        INDArray[] output = initializedModel.output(false, image);
        
        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
        INDArray image = loader.asMatrix(new File("data/imagematch/car.jpg"));
        DataNormalization scaler = new VGG16ImagePreProcessor();
        scaler.transform(image);
        INDArray[] output = initializedModel.output(false, image);

        // check output labels of result
        String decodedLabels = new ImageNetLabels().decodePredictions(output[0]);
        System.out.println(decodedLabels);
	}
}
