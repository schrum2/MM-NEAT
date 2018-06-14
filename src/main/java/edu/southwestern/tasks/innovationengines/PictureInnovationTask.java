package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.ImageNetClassification;

public class PictureInnovationTask<T extends Network> extends LonerTask<T> {
	// Setting determines whether the mean of the ImageNet training set is subtracted from the image
	// before classification, since this is how VGG16 was trained for ImageNet. However, it is not clear
	// if such pre-processing is appropriate for the other ImageNet models.
	private static final boolean PREPROCESS = true;
	private double pictureInnovationSaveThreshold = Parameters.parameters.doubleParameter("pictureInnovationSaveThreshold");
	
	@Override
	public int numObjectives() {
		return 0; // None: only behavior characterization
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		Network cppn = individual.getPhenotype();
		BufferedImage image = GraphicsUtil.imageFromCPPN(cppn, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT);
		INDArray imageArray = ImageNetClassification.bufferedImageToINDArray(image);
		INDArray scores = ImageNetClassification.getImageNetPredictions(imageArray, PREPROCESS);
		ArrayList<Double> behaviorVector = ArrayUtil.doubleVectorFromINDArray(scores);
		Score<T> result = new Score<>(individual, new double[]{}, behaviorVector);
		if(CommonConstants.watch) {
			DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT);
			// Prints top 4 labels
			String decodedLabels = ImageNetClassification.getImageNetLabelsInstance().decodePredictions(scores);
			System.out.println(decodedLabels);
			// Wait for user
			MiscUtil.waitForReadStringAndEnterKeyPress();
			picture.dispose();
		}
		if(CommonConstants.netio) {
			// Lot of duplication of computation from Archive. Can that be fixed?
			@SuppressWarnings("unchecked")
			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			for(int i = 0; i < binLabels.size(); i++) {
				Score<T> elite = archive.getElite(i);
				// If the bin is empty, or the candidate is better than the elite for that bin's score
				double binScore = result.behaviorVector.get(i);
				if(elite == null || binScore > elite.behaviorVector.get(i)) {
					if(binScore > pictureInnovationSaveThreshold) {
						String fileName = String.format("%7.5f", binScore) + binLabels.get(i) + individual.getId() + ".jpg";
						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(i);
						String fullName = binPath + File.separator + fileName;
						System.out.println(fullName);
						GraphicsUtil.saveImage(image, fullName);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * Save fresh archive of only the final images
	 */
	public void finalCleanup() {
		System.out.println("Save images of all final elites");
		int saveWidth = Parameters.parameters.integerParameter("imageWidth"); 
		int saveHeight = Parameters.parameters.integerParameter("imageHeight");
		// Save a collection of only the final images from each MAP Elites bin
		if(CommonConstants.netio) {
			@SuppressWarnings("unchecked")
			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
			String finalArchive = archive.getArchiveDirectory() + "Final";
			new File(finalArchive).mkdir(); // Make different directory
			List<String> binLabels = archive.getBinMapping().binLabels();
			for(int i = 0; i < binLabels.size(); i++) {
				String label = binLabels.get(i);
				Score<T> score = archive.getElite(i);
				Network cppn = score.individual.getPhenotype();
				BufferedImage image = GraphicsUtil.imageFromCPPN(cppn, saveWidth, saveHeight);
				double binScore = score.behaviorVector.get(i);
				String fileName = String.format("%7.5f", binScore) + label + ".jpg";
				String fullName = finalArchive + File.separator + fileName;
				GraphicsUtil.saveImage(image, fullName);
			}
		}
	}


	public int numCPPNInputs() {
		return PicbreederTask.CPPN_NUM_INPUTS;
	}

	public int numCPPNOutputs() {
		return PicbreederTask.CPPN_NUM_OUTPUTS;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// For getting pictures from a crashed/finished run
//		MMNEAT.main(new String[]{"runNumber:1","randomSeed:0","base:innovation","mu:400","maxGens:1", // Terminate after writing final archive 
//				"io:false","netio:true",
//				"log:InnovationPictures-VGG19Model","saveTo:VGG19Model"}); 
		
		
		// For test runs
		MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","base:innovation","mu:400","maxGens:2000000",
				"io:true","netio:true","mating:true","task:edu.southwestern.tasks.innovationengines.PictureInnovationTask",
				"log:InnovationPictures-AllModels","saveTo:AllModels","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:true",
				"ea:edu.southwestern.evolution.mapelites.MAPElites",
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.ImageNetBinMapping","fs:true",
				//"imageNetModel:edu.southwestern.networks.dl4j.VGG19Wrapper",
				//"imageNetModel:edu.southwestern.networks.dl4j.VGG16Wrapper",
				"imageNetModel:edu.southwestern.networks.dl4j.AverageAllZooModelImageNetModels",
				"pictureInnovationSaveThreshold:0.5",
				"imageWidth:500","imageHeight:500", // Final save size
				"includeSigmoidFunction:true", // In original Innovation Engine
				"includeTanhFunction:false",
				"includeIdFunction:false",
				"includeFullApproxFunction:false",
				"includeApproxFunction:false",
				"includeGaussFunction:true", // In original Innovation Engine
				"includeSineFunction:true", // In original Innovation Engine
				"includeSawtoothFunction:true", // Added 
				"includeAbsValFunction:true", // Added
				"includeHalfLinearPiecewiseFunction:true", // In original Innovation Engine
				"includeStretchedTanhFunction:false",
				"includeReLUFunction:false",
				"includeSoftplusFunction:false",
				"includeLeakyReLUFunction:false",
				"includeFullSawtoothFunction:false",
				"includeTriangleWaveFunction:false", 
				"includeSquareWaveFunction:false"}); 
	}
}
