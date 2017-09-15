package org.deeplearning4j.examples.vgg16;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.ImageNetClassification;

@SuppressWarnings("unused")
public class VGG16Test {

	// Consider turning this file into a unit test instead
	public static void main(String[] args) throws IOException {
		// From the original Picbreeder
		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/butterfly.jpg")), true); // VGG16: hair_slide: 69%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/fly-fly.jpg")), true); // VGG16: hair_slide: 12%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/latte-cup.jpg")), true); // VGG16: cup: 8%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/parrot-toucan.jpg")), true); // VGG16: mouse: 15%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/race_car-racer.jpg")), true); // VGG16: speedboat: 68%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_crayon-lipstick.jpg")), true); // VGG16: cup: 14%
		MiscUtil.waitForReadStringAndEnterKeyPress();
		results(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_skull.jpg")), true); // VGG16: crash_helmet: 45%
		MiscUtil.waitForReadStringAndEnterKeyPress();

		
		
//		NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
//		results(loader.asMatrix(new File("data/imagematch/car.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		results(loader.asMatrix(new File("data/imagematch/cat.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();		
//		results(loader.asMatrix(new File("data/imagematch/organimage.bmp")), false);
//		MiscUtil.waitForReadStringAndEnterKeyPress();		
//		results(loader.asMatrix(new File("data/imagematch/sadsheepimage.bmp")), false);
//		MiscUtil.waitForReadStringAndEnterKeyPress();		
//		results(loader.asMatrix(new File("data/imagematch/supercreepypersonimage.jpg")), false);
//		MiscUtil.waitForReadStringAndEnterKeyPress();		
		
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
	
	public static void results(INDArray image, boolean preprocess) {
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
