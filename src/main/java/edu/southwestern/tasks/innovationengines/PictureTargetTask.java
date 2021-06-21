package edu.southwestern.tasks.innovationengines;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import autoencoder.python.AutoEncoderProcess;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNPlusParametersGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.BinLabels;
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
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

public class PictureTargetTask<T extends Network> extends LonerTask<T> {
	
	public static final String IMAGE_MATCH_PATH = "data" + File.separator + "imagematch";
//	private static final int IMAGE_PLACEMENT = 200;
//	private static final int HUE_INDEX = 0;
//	private static final int SATURATION_INDEX = 1;
//	private static final int BRIGHTNESS_INDEX = 2;
//	private Network individual;
	private BufferedImage img = null;
	public static int imageHeight, imageWidth;
	private double fitnessSaveThreshold = Parameters.parameters.doubleParameter("fitnessSaveThreshold");
	private double[] targetImageFeatures; 
	
	private static final double MODULATION_PARAMETER_ALPHA = 5;

	//private ArrayList<Pair<double[], double[]>> trainingPairs;
	
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
		
		targetImageFeatures = GraphicsUtil.flatFeatureArrayFromBufferedImage(img);
		//trainingPairs = ImageMatchTask.getImageMatchTrainingPairs(img);
	}
	
	@Override
	public int numObjectives() {
		return 1; // Just comparison score to target image
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	/**
	 * Calculating equation 1 in the Woolley paper.
	 * 
	 * @param candidateFeature represented by the letter c in Woolley paper
	 * @param targetFeature represented by the letter t in Woolley paper
	 * @return returns the difference between two features c and t
	 */
	public static double degreeOfDifference(double candidateFeature, double targetFeature) {
		return 1 - (Math.exp(-MODULATION_PARAMETER_ALPHA * Math.abs(candidateFeature - targetFeature))); // Function d from Woolley paper
	}
	
	/**
	 * Calculates the error between candidate and target feature sets (equation 2
	 * from the Woolley paper, err(C,T)).
	 * 
	 * @param candidateFeatures an array of candidate features represented by C in the Woolley paper
	 * @param targetFeatures an array of target features represented by T in the Woolley paper
	 * @return returns the error between candidate and target feature sets
	 */
	public static double candidateVsTargetError(double[] candidateFeatures, double[] targetFeatures) {
		assert candidateFeatures.length == targetFeatures.length: "Number of candidate and target features needs to match";
		double sum = 0;
		for (int i = 0; i < candidateFeatures.length; i++) {
			sum += degreeOfDifference(candidateFeatures[i], targetFeatures[i]);
		}
		return sum / candidateFeatures.length; // This is the err function from the Woolley paper
	}
	
	/**
	 * Fitness is defined as 1 - the squared error between the candidate and 
	 * the target.  This is equation 3 from the Woolley paper (f(C)).
	 * 
	 * @param candidateImage the candidate image produced by the CPPN
	 * @return returns the fitness of the candidate image
	 */
	
	public double fitness(BufferedImage candidateImage) {
		
		double[] candidateFeatures = GraphicsUtil.flatFeatureArrayFromBufferedImage(candidateImage);
		
		// Using the fitness calculation from the Woolley paper		
		if(Parameters.parameters.booleanParameter("useWoolleyImageMatchFitness")) {
			double error = candidateVsTargetError(candidateFeatures, targetImageFeatures);
			return 1 - error * error;
		} else if (Parameters.parameters.booleanParameter("useRMSEImageMatchFitness")) {
			return rootMeanSquareErrorFitness(candidateFeatures, targetImageFeatures);
		} else {
			throw new IllegalStateException("Proper fitness function for PictureTargetTask not specified");
		}
		
		// else if useRMSEImageMatchFitness then return rootMeanSquareErrorFitness (need new command line parameter)
		
		// else throw new IllegalStateException("Proper fitness function for PictureTargetTask not specified");
	}
	
	/**
	 * Calculates fitness of the candidate features to the target features
	 * using root mean square error.
	 * 
	 * @param candidateFeatures an array of candidate features represented by C in the Woolley paper
	 * @param targetFeatures an array of target features represented by T in the Woolley paper
	 * @return the fitness of the candidateFeatures using root mean square error 
	 */
	public static double rootMeanSquareErrorFitness(double[] candidateFeatures, double[] targetFeatures) {
		// 1 minus the error because we want to minimize the error
		return 1 - StatisticsUtilities.rootMeanSquareError(targetFeatures, candidateFeatures);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		Network cppn = individual.getPhenotype();
		BufferedImage image = PicbreederTask.imageFromCPPN(cppn, imageWidth, imageHeight, ArrayUtil.doubleOnes(cppn.numInputs()));
		TWEANNGenotype tweannIndividual = (individual instanceof TWEANNGenotype ? (TWEANNGenotype) individual : ((TWEANNPlusParametersGenotype<ArrayList<Double>>) individual).getTWEANNGenotype());
		
		// Need to assign values
		int[] indicesMAPEliteBin = null;
		
		if(MMNEAT.ea instanceof MAPElites) {
			int nodes = Math.min(tweannIndividual.nodes.size(), CPPNComplexityBinLabels.maxNumNeurons);
			if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof CPPNComplexityBinLabels) {
				// What if number of nodes or links exceeds 35? Need to cap the index
				int links = Math.min(tweannIndividual.links.size(), CPPNComplexityBinLabels.maxNumLinks);
				indicesMAPEliteBin = new int[] {nodes, links}; // Array of two values corresponding to bin label dimensions
			} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof PictureFourQuadrantBrightnessBinLabels) {
				PictureFourQuadrantBrightnessBinLabels labels = (PictureFourQuadrantBrightnessBinLabels) ((MAPElites<T>) MMNEAT.ea).getBinLabelsClass();
				indicesMAPEliteBin = labels.binCoordinates(image);
			} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof GaierAutoencoderPictureBinLabels) {
				// If the AutoEncoder has not been initialized yet, then loss is 1.0
				double loss = AutoEncoderProcess.neverInitialized ? 1.0 : AutoEncoderProcess.getReconstructionLoss(image);
				
				// TODO Change
				//int lossIndex = (int) Math.min(Math.floor(loss * GaierAutoencoderPictureBinLabels.numLossBins),GaierAutoencoderPictureBinLabels.numLossBins - 1);
				int lossIndex = Math.min((int) Math.max(0, ((loss - Parameters.parameters.doubleParameter("minAutoencoderLoss")) / (Parameters.parameters.doubleParameter("maxAutoencoderLoss")) * GaierAutoencoderPictureBinLabels.numLossBins)), GaierAutoencoderPictureBinLabels.numLossBins - 1);
				indicesMAPEliteBin = new int[]{nodes, lossIndex};
			} else if(((MAPElites<T>) MMNEAT.ea).getBinLabelsClass() instanceof CPPNNeuronCountBinLabels) {
				assert nodes >= CPPNNeuronCountBinLabels.MIN_NUM_NEURONS : "Why so few neurons? " + nodes + "\n" + tweannIndividual;
				indicesMAPEliteBin = new int[] {nodes};
			} else {
				throw new IllegalStateException("No valid binning scheme provided for PictureTargetTask");
			}
		}
		
		double binScore = fitness(image);
		
		Score<T> result = new Score<>(individual, new double[]{binScore}, indicesMAPEliteBin, binScore);
		if(CommonConstants.watch) {
			BufferedImage view = PicbreederTask.imageFromCPPN(cppn, Parameters.parameters.integerParameter("imageWidth"), Parameters.parameters.integerParameter("imageHeight"), ArrayUtil.doubleOnes(cppn.numInputs()));
			DrawingPanel picture = GraphicsUtil.drawImage(view, "Image", Parameters.parameters.integerParameter("imageWidth"), Parameters.parameters.integerParameter("imageHeight"));
			System.out.println("Score: "+binScore);
			// Wait for user
			MiscUtil.waitForReadStringAndEnterKeyPress();
			picture.dispose();
		}
		if(CommonConstants.netio) {
			//System.out.println("Save archive images");
			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			// Index in flattened bin array
			Score<T> elite = archive.getElite(indicesMAPEliteBin);
			// If the bin is empty, or the candidate is better than the elite for that bin's score
			if(elite == null || binScore > elite.behaviorIndexScore()) {
				if(binScore > fitnessSaveThreshold) {
					String fileName = String.format("%7.5f", binScore) + binLabels.get(archive.getBinMapping().oneDimensionalIndex(indicesMAPEliteBin)) + individual.getId() + ".jpg";
					String archivePath = archive.getArchiveDirectory();
					File archiveDir = new File(archivePath);
					if(!archiveDir.exists()) archiveDir.mkdir();
					String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(archive.getBinMapping().oneDimensionalIndex(indicesMAPEliteBin));
					File bin = new File(binPath);
					if(!bin.exists()) bin.mkdir();
					String fullName = binPath + File.separator + fileName;
					System.out.println(fullName);
					GraphicsUtil.saveImage(image, fullName);
				}
			}
			
			
			
//			// Lot of duplication of computation from Archive. Can that be fixed?
//			@SuppressWarnings("unchecked")
//			Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
//				Score<T> elite = archive.getElite(i);
//				// If the bin is empty, or the candidate is better than the elite for that bin's score
//				binScore = result.getTraditionalDomainSpecificBehaviorVector().get(i);
//				if(elite == null || binScore > elite.getTraditionalDomainSpecificBehaviorVector().get(i)) {  // Duplicate variable error, does this need to be different from the binScore in the evaluate method?
//					if(binScore > fitnessSaveThreshold) {
//						String fileName = String.format("%7.5f", binScore) + binLabels.get(i) + individual.getId() + ".jpg";						
//						String archivePath = archive.getArchiveDirectory();
//						File archiveDir = new File(archivePath);
//						if(!archiveDir.exists()) archiveDir.mkdir();
//						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(i);
//						File bin = new File(binPath);
//						if(!bin.exists()) bin.mkdir();
//						String fullName = binPath + File.separator + fileName;
//						System.out.println(fullName);
//						GraphicsUtil.saveImage(image, fullName);
//					}
//				}
			
		}
		return result;
	}
	
	/**
	 * Saves all the images in the image archive in 
	 * a new directory inside the experiment directory.
	 * 
	 * @param directoryName Name of the directory where the images
	 *                      will be saved
	 */
	@SuppressWarnings("unchecked")
	public void saveAllArchiveImages(String directoryName, int saveWidth, int saveHeight) {
		String snapshot = FileUtilities.getSaveDirectory() + File.separator + "snapshots";
		File snapshotDir = new File(snapshot);
		if(!snapshotDir.exists()) snapshotDir.mkdir();
		
		String subdir = snapshot + File.separator + directoryName;
		File subdirFile = new File(subdir);
		subdirFile.mkdir();
		
		Archive<T> archive = ((MAPElites<T>) MMNEAT.ea).getArchive();
		BinLabels labels = archive.getBinMapping();
//		int saveWidth = Parameters.parameters.integerParameter("imageWidth"); 
//		int saveHeight = Parameters.parameters.integerParameter("imageHeight");
		
		for(Score<T> s : archive.getArchive()) {
			if(s != null) {
				Network cppn = s.individual.getPhenotype();
				BufferedImage image = PicbreederTask.imageFromCPPN(cppn, saveWidth, saveHeight, ArrayUtil.doubleOnes(cppn.numInputs()));
				//String fullName = finalArchive + File.separator + fileName;
				String fullName = subdir + File.separator + s.behaviorIndexScore() + "-" + labels.binLabels().get(labels.oneDimensionalIndex(s.MAPElitesBinIndex()))+".jpg";
				GraphicsUtil.saveImage(image, fullName);
			}
		}
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
				if(score != null) {
					Network cppn = score.individual.getPhenotype();
					BufferedImage image = GraphicsUtil.imageFromCPPN(cppn, saveWidth, saveHeight);	// not sure if this one should be deleted, (imageFromCPPN vs ImageNetClassification)
					double binScore = score.behaviorIndexScore(i);
					String fileName = String.format("%7.5f", binScore) + label + ".jpg";
					String fullName = finalArchive + File.separator + fileName;
					GraphicsUtil.saveImage(image, fullName);
				}
			}
		}
	}


	public int numCPPNInputs() {
		return PicbreederTask.CPPN_NUM_INPUTS;
	}

	public int numCPPNOutputs() {
		return PicbreederTask.CPPN_NUM_OUTPUTS;
	}

	
//	@SuppressWarnings("unchecked")
//	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		Parameters.initializeParameterCollections(new String[] {"runNumber:1","randomSeed:0","base:targetimage","mu:400","maxGens:2000000",
//				"io:true","netio:true","mating:true","task:edu.southwestern.tasks.innovationengines.PictureTargetTask",
//				"log:TargetImage-TESTING","saveTo:TESTING","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
//				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:false",
//				"ea:edu.southwestern.evolution.mapelites.MAPElites",
//				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
//				//"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.CPPNComplexityBinMapping",
//				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.PictureFourQuadrantBrightnessBinLabels",
//				"fs:true",
//				"useWoolleyImageMatchFitness:false", "useRMSEImageMatchFitness:true", // Pick one
//				//"matchImageFile:TexasFlag.png",
//				//"matchImageFile:cat.jpg",
//				"matchImageFile:skull64.jpg",
//				"fitnessSaveThreshold:0.7",		// Higher threshold for RMSE 
//				"includeSigmoidFunction:true", 	// In Brian Woolley paper
//				"includeTanhFunction:false",
//				"includeIdFunction:true",		// In Brian Woolley paper
//				"includeFullApproxFunction:false",
//				"includeApproxFunction:false",
//				"includeGaussFunction:true", 	// In Brian Woolley paper
//				"includeSineFunction:true", 	// In Brian Woolley paper
//				"includeCosineFunction:true", 	// In Brian Woolley paper
//				"includeSawtoothFunction:false", 
//				"includeAbsValFunction:false", 
//				"includeHalfLinearPiecewiseFunction:false", 
//				"includeStretchedTanhFunction:false",
//				"includeReLUFunction:false",
//				"includeSoftplusFunction:false",
//				"includeLeakyReLUFunction:false",
//				"includeFullSawtoothFunction:false",
//				"includeTriangleWaveFunction:false", 
//				"includeSquareWaveFunction:false", "blackAndWhitePicbreeder:true"});
//		MMNEAT.loadClasses();
//		// TODO: Init the picture target task, create the initial archive, then save immediately (test saveAllArchiveImages)
//		PictureTargetTask<TWEANN> skull = (PictureTargetTask<TWEANN>) MMNEAT.task;
//		
//		skull.saveAllArchiveImages("iteration");
//	}	
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		
		// For test runs
		MMNEAT.main(new String[]{"runNumber:30","randomSeed:30","base:targetimage","mu:400","maxGens:100000000",
				"io:true","netio:true","mating:true","task:edu.southwestern.tasks.innovationengines.PictureTargetTask",
				"log:TargetImage-skullAutoEncoderNeuronBinningRegularGenotype","saveTo:skullAutoEncoderNeuronBinningRegularGenotype",
				"allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:false",
				"ea:edu.southwestern.evolution.mapelites.MAPElites",
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
				//"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.CPPNComplexityBinLabels",
				//"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.GaierAutoencoderPictureBinLabels",
				//"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.PictureFourQuadrantBrightnessBinLabels",
				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.CPPNNeuronCountBinLabels",
				"fs:true",
				//"genotype:edu.southwestern.evolution.genotypes.EnhancedCPPNPictureGenotype",
				"trainingAutoEncoder:true",
				"useWoolleyImageMatchFitness:false", "useRMSEImageMatchFitness:true", // Pick one
				//"matchImageFile:TexasFlag.png",
				//"matchImageFile:cat.jpg",
				"matchImageFile:skull64.jpg",
				"fitnessSaveThreshold:1.0",		// Since we periodically save the whole archive, don't bother saving with threshold any more 
				//"imageArchiveSaveFrequency:50000",
				"imageArchiveSaveFrequency:1000",
				"includeSigmoidFunction:true", 	// In Brian Woolley paper
				"includeTanhFunction:false",
				"includeIdFunction:true",		// In Brian Woolley paper
				"includeFullApproxFunction:false",
				"includeApproxFunction:false",
				"includeGaussFunction:true", 	// In Brian Woolley paper
				"includeSineFunction:true", 	// In Brian Woolley paper
				"includeCosineFunction:true", 	// In Brian Woolley paper
				"includeSawtoothFunction:false", 
				"includeAbsValFunction:false", 
				"includeHalfLinearPiecewiseFunction:false", 
				"includeStretchedTanhFunction:false",
				"includeReLUFunction:false",
				"includeSoftplusFunction:false",
				"includeLeakyReLUFunction:false",
				"includeFullSawtoothFunction:false",
				"includeTriangleWaveFunction:false", 
				"includeSquareWaveFunction:false", "blackAndWhitePicbreeder:true",
				"deleteOldArchives:true", "dynamicAutoencoderIntervals:true"}); 
	}
}
