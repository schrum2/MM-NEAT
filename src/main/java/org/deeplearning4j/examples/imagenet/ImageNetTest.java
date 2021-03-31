package org.deeplearning4j.examples.imagenet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.ZooType;
import org.deeplearning4j.zoo.util.imagenet.ImageNetLabels;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import org.datavec.image.transform.ColorConversionTransform;

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
		// ConcatApproach is incompatible with some of the standard ImageNet helper methods
		//Parameters.initializeParameterCollections(new String[] {"imageNetModel:edu.southwestern.networks.dl4j.ConcatAllZooModelImageNetModels"});		
		//Parameters.initializeParameterCollections(new String[] {"imageNetModel:edu.southwestern.networks.dl4j.MinAllZooModelImageNetModels"});		
		//Parameters.initializeParameterCollections(new String[] {"imageNetModel:edu.southwestern.networks.dl4j.VGG16Wrapper"});		
		Parameters.initializeParameterCollections(new String[] {"imageNetModel:edu.southwestern.networks.dl4j.VGG19Wrapper"});		
		AllZooModelImageNetModels.initAllImageNets();
		NativeImageLoader loader = new NativeImageLoader(ImageNetClassification.IMAGE_NET_INPUT_HEIGHT, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_CHANNELS);
		
		// GoogLeNet supposedly uses RGB vs BGR format, making this form of image loading appropriate
		//NativeImageLoader loader = new NativeImageLoader(ImageNetClassification.IMAGE_NET_INPUT_HEIGHT, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_CHANNELS, new ColorConversionTransform(org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2RGB));
		
		// From the original Picbreeder: Note: All VGG-16 predictions consistent with https://deeplearning4j.org/demo-classifier-vgg16
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/butterfly.jpg")), true); 
//		// VGG16: hair_slide: 69%, VGG19: hair_slide: 74%, GoogLeNet: hair_slide: 9%, ResNet50: hair_slide: 51%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/fly-fly.jpg")), true); 
//		// VGG16: hair_slide: 12%, VGG19: hair_slide: 22%, GoogLeNet: safety_pin: 4%, ResNet50: hair_slide: 14%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/latte-cup.jpg")), true); 
//		// VGG16: cup: 8%, VGG19: cup: 14%, GoogLeNet: buckle: 6%, ResNet50: mask: 44%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/parrot-toucan.jpg")), true); 
//		// VGG16: mouse: 15%, VGG19: toucan: 16%, GoogLeNet: nipple: 10%, ResNet50: maraca: 19%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/race_car-racer.jpg")), true); 
//		// VGG16: speedboat: 68%, VGG19: sports_car: 23%, GoogLeNet: binoculars: 12%, ResNet50: slot: 60%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_crayon-lipstick.jpg")), true); 
//		// VGG16: cup: 14%, VGG19: hook: 9%, GoogLeNet: lipstick: 10%, ResNet50: red_wine: 92%
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("d:/TEMP/FromRealPicbreeder/red_skull.jpg")), true); 
//		// VGG16: crash_helmet: 45%, VGG19: crash_helmet: 39%, GoogLeNet: ski_mask: 18%, ResNet50: ski_mask: 89%
//		MiscUtil.waitForReadStringAndEnterKeyPress();

//		resultsFromOne(loader.asMatrix(new File("D:\\TEMP\\VGG19Test\\cat-getty_0.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("D:\\TEMP\\VGG19Test\\thul-IMG_2100.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("D:\\TEMP\\VGG19Test\\140272627-grooming-needs-senior-cat-632x475.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromOne(loader.asMatrix(new File("D:\\TEMP\\VGG19Test\\6becb852b27e1d80fbd03048dfb377a5_1273011771.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();		
		
//		resultsFromAll(loader.asMatrix(new File("innovation\\AllMinimumModel0\\archive\\window_shade\\0.99997window_shade6023.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
//		resultsFromAll(loader.asMatrix(new File("innovation\\AllMinimumModel0\\archive\\window_shade\\0.99997window_shade6023.jpg")), false);
//		MiscUtil.waitForReadStringAndEnterKeyPress();

//		resultsFromAll(loader.asMatrix(new File("innovation\\AllMinimumModel0\\archive\\jellyfish\\0.71373jellyfish967.jpg")), true);
//		resultsFromOne(loader.asMatrix(new File("innovation\\AllMinimumModel0\\archive\\jellyfish\\0.71373jellyfish967.jpg")), true);
//		MiscUtil.waitForReadStringAndEnterKeyPress();	
		
		resultsFromOne(loader.asMatrix(new File("data/imagematch/quail.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/quail.JPEG")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/hay.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/hay.JPEG")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/cropped_panda.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/cropped_panda.jpg")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/rabbitAndKitten.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/rabbitAndKitten.jpg")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/jellyfish.JPEG")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/blackwidow.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/blackwidow.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/blackwidow.JPEG")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/scorpion.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/scorpion.JPEG")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/scorpion.JPEG")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/car.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/car.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/car.jpg")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();
		//resultsFromAll(loader.asMatrix(new File("data/imagematch/cat.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/cat.jpg")), true);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/cat.jpg")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/organimage.bmp")), false);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/organimage.bmp")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/sadsheepimage.bmp")), false);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/sadsheepimage.bmp")), false);
		MiscUtil.waitForReadStringAndEnterKeyPress();		
		resultsFromAll(loader.asMatrix(new File("data/imagematch/supercreepypersonimage.jpg")), false);
		resultsFromOne(loader.asMatrix(new File("data/imagematch/supercreepypersonimage.jpg")), false);
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
			String decodedLabels = ImageNetClassification.getImageNetLabelsInstance().decodePredictions(scores);
			System.out.println(key + ":\n" + decodedLabels);
		}
	}
	
	public static void resultsFromOne(INDArray image, boolean preprocess) {
		INDArray scores = ImageNetClassification.getImageNetPredictions(image, preprocess);
//		Map<String,Float> results = ImageNetClassification.getImageNetLabelledPredictions(scores);
//		for(String label : results.keySet()) {
//			System.out.println(label + ":" + results.get(label));
//		}
		System.out.println("BEST: " + ImageNetClassification.bestLabel(scores));
		// check output labels of result
		String decodedLabels = ImageNetClassification.getImageNetLabelsInstance().decodePredictions(scores);
		System.out.println(decodedLabels);
	}
}
