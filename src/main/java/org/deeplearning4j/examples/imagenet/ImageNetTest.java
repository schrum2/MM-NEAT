package org.deeplearning4j.examples.imagenet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.zoo.ModelSelector;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.ZooType;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.dl4j.AllZooModelImageNetModels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.ImageNetClassification;

@SuppressWarnings("unused")
public class ImageNetTest {

	// Consider turning this file into a unit test instead
	public static void main(String[] args) throws IOException, InterruptedException {
		Parameters.initializeParameterCollections(new String[] {}); // default parameters		
		AllZooModelImageNetModels.initAllImageNets();
		
		// From the original Picbreeder
//		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/butterfly.jpg")), true); 
//		// VGG16: hair_slide: 69%, VGG19: hair_slide: 74%, GoogLeNet: hair_slide: 9%, ResNet50: hair_slide: 51%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/fly-fly.jpg")), true); 
//		// VGG16: hair_slide: 12%, VGG19: hair_slide: 22%, GoogLeNet: safety_pin: 4%, ResNet50: hair_slide: 14%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/latte-cup.jpg")), true); 
//		// VGG16: cup: 8%, VGG19: cup: 14%, GoogLeNet: buckle: 6%, ResNet50: mask: 44%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/parrot-toucan.jpg")), true); 
//		// VGG16: mouse: 15%, VGG19: toucan: 16%, GoogLeNet: nipple: 10%, ResNet50: maraca: 19%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/race_car-racer.jpg")), true); 
//		// VGG16: speedboat: 68%, VGG19: sports_car: 23%, GoogLeNet: binoculars: 12%, ResNet50: slot: 60%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_crayon-lipstick.jpg")), true); 
//		// VGG16: cup: 14%, VGG19: hook: 9%, GoogLeNet: lipstick: 10%, ResNet50: red_wine: 92%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_skull.jpg")), true); 
//		// VGG16: crash_helmet: 45%, VGG19: crash_helmet: 39%, GoogLeNet: ski_mask: 18%, ResNet50: ski_mask: 89%
//		MiscUtil.waitForReadStringAndEnterKeyPress();

		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
//		resultsFromAll(loader.asMatrix(new File("innovation/Pictures0/archive/pinwheel/1.00000pinwheel98544.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromAll(loader.asMatrix(new File("innovation/Pictures0/archive/peacock/0.99997peacock104176.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();

		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/car.jpg")), true);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		resultsFromAll(loader.asMatrix(new File("data/imagematch/cat.jpg")), true);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/organimage.bmp")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/sadsheepimage.bmp")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/supercreepypersonimage.jpg")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		
//		Parameters.initializeParameterCollections(new String[]{"runNumber:0","randomSeed:0","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:true","task:edu.southwestern.tasks.interactive.picbreeder.PicbreederTask","allowMultipleFunctions:true","ftype:0","watch:false","netChangeActivationRate:0.3","cleanFrequency:-1","recurrency:false","cleanOldNetworks:false","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageSize:224"});
//		MMNEAT.loadClasses();
//		
//		while(true) {
//			TWEANNGenotype tg = new TWEANNGenotype();
//			for(int i = 0; i < 15; i++) {
//				tg.mutate();
//			}
//			BufferedImage image = GraphicsUtil.imageFromCPPN(tg.getPhenotype(), 224, 224);
//			DrawingPanel p = GraphicsUtil.drawImage(image, "test", 224,224);
//			results(ImageNetClassification.bufferedImageToINDArray(image), false);
//			MiscUtil.waitForReadStringAndEnterKeyPress();				
//		}
	}
	
	public static void resultsFromAll(INDArray image, boolean preprocess) {
		if(preprocess) {
			ImageNetClassification.imagePreprocess(image);
		}
		Map<String, INDArray> allScores = AllZooModelImageNetModels.runAllModels(image);
		for(String key : allScores.keySet()) {
			INDArray scores = allScores.get(key);
			String decodedLabels = new ImageNetLabels().decodePredictions(scores);
			System.out.println(key + ":\n" + decodedLabels);
		}
	}
	
	public static void resultsFromOne(INDArray image, boolean preprocess) {
		INDArray scores = ImageNetClassification.getImageNetPredictions(image, preprocess);
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
