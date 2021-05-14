package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.tasks.testmatch.MatchDataTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.ImageNetClassification;

public class PictureTargetTask<T extends Network> extends LonerTask<T> {
	
	public static final String IMAGE_MATCH_PATH = "data" + File.separator + "imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int HUE_INDEX = 0;
	private static final int SATURATION_INDEX = 1;
	private static final int BRIGHTNESS_INDEX = 2;
	private Network individual;
	private BufferedImage img = null;
	public int imageHeight, imageWidth;
	private double fitnessSaveThreshold = 0;

	
	public PictureTargetTask(){
		this(Parameters.parameters.stringParameter("matchImageFile"));
		MatchDataTask.pauseForEachCase = false;
	}
	
	public PictureTargetTask(String filename) {
		try {// throws and exception if filename is not valid
			img = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + filename));
		} catch (IOException e) {
			System.out.println("Could not load image: " + filename);
			System.exit(1);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
	}
	
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
		BufferedImage image = GraphicsUtil.imageFromCPPN(cppn, ImageNetClassification.TARGET_INPUT_WIDTH, ImageNetClassification.TARGET_INPUT_HEIGHT);
		TWEANNGenotype tweannIndividual = (TWEANNGenotype) individual;
		// TODO: What if number of nodes or links exceeds 35? Need to cap the index
		int[] indicesMAPEliteBin = new int[] {tweannIndividual.nodes.size(), tweannIndividual.links.size()}; // Array of two values corresponding to bin label dimensions
		double binScore = 0.0; // A match score fitness calculated similarly to ImageMatchTask/MatchDataTask
		
		Score<T> result = new Score<>(individual, new double[]{}, indicesMAPEliteBin, binScore);
		if(CommonConstants.watch) {
			DrawingPanel picture = GraphicsUtil.drawImage(image, "Image", ImageNetClassification.TARGET_INPUT_WIDTH, ImageNetClassification.TARGET_INPUT_HEIGHT);
			// Prints top 4 labels
//			String decodedLabels = ImageNetClassification.getImageNetLabelsInstance().decodePredictions(scores);
//			System.out.println(decodedLabels);
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
				binScore = result.getTraditionalDomainSpecificBehaviorVector().get(i);
				if(elite == null || binScore > elite.getTraditionalDomainSpecificBehaviorVector().get(i)) {  // Duplicate variable error, does this need to be different from the binScore in the evaluate method?
					if(binScore > fitnessSaveThreshold) {
						String fileName = String.format("%7.5f", binScore) + binLabels.get(i) + individual.getId() + ".jpg";						
						String archivePath = archive.getArchiveDirectory();
						File archiveDir = new File(archivePath);
						if(!archiveDir.exists()) archiveDir.mkdir();
						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(i);
						File bin = new File(binPath);
						if(!bin.exists()) bin.mkdir();
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
				BufferedImage image = GraphicsUtil.imageFromCPPN(cppn, saveWidth, saveHeight);	// not sure if this one should be deleted, (imageFromCPPN vs ImageNetClassification)
				double binScore = score.behaviorIndexScore(i);
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
				"log:InnovationPictures-VGG19","saveTo:VGG19","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:true",
				"ea:edu.southwestern.evolution.mapelites.MAPElites",
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.ImageNetBinMapping","fs:true",
				//"imageNetModel:edu.southwestern.networks.dl4j.VGG19Wrapper",
				"imageNetModel:edu.southwestern.networks.dl4j.VGG16Wrapper",
				//"imageNetModel:edu.southwestern.networks.dl4j.AverageAllZooModelImageNetModels",
				"pictureInnovationSaveThreshold:0.3",
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
