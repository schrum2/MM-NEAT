package edu.southwestern.tasks.innovationengines;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.api.ndarray.INDArray;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.objectbreeder.ThreeDimensionalObjectBreederTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triangle;
import edu.southwestern.util.graphics.AnimationUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.graphics.ImageNetClassification;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;

public class ShapeInnovationTask extends LonerTask<Pair<TWEANN, ArrayList<Double>>> implements BoundedTask {
	
	public static final int INDEX_RED = 0;
	public static final int INDEX_GREEN = 1;
	public static final int INDEX_BLUE = 2;
	private static final int INDEX_PITCH = 3;
	private static final int INDEX_HEADING = 4;
	
	// Setting determines whether the mean of the ImageNet training set is subtracted from the image
	// before classification, since this is how VGG16 was trained for ImageNet. However, it is not clear
	// if such pre-processing is appropriate for the other ImageNet models.
	private static final boolean PREPROCESS = true;
	private double pictureInnovationSaveThreshold = Parameters.parameters.doubleParameter("pictureInnovationSaveThreshold");
	
	private int numImageSamples = Parameters.parameters.integerParameter("numShapeInnovationSamples");
	private boolean vertical = false;
	private double[] lower = new double[]{0,0,0,0,0}; // Background color (first three) and pitch, heading
	private double[] upper = new double[]{1,1,1,1,1}; // Background color (first three) and pitch, heading
	
	@Override
	public int numObjectives() {
		return 0; // None: only behavior characterization
	}

	@Override
	public double getTimeStamp() {
		return 0; // Not used
	}

	@Override
	public Score<Pair<TWEANN, ArrayList<Double>>> evaluate(Genotype<Pair<TWEANN, ArrayList<Double>>> individual) {
		Pair<TWEANN,ArrayList<Double>> pair = individual.getPhenotype();
		Network cppn = pair.t1;
		// Get the shape
		List<Triangle> tris = ThreeDimensionalUtil.trianglesFromCPPN(cppn, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT, ThreeDimensionalObjectBreederTask.CUBE_SIDE_LENGTH, ThreeDimensionalObjectBreederTask.SHAPE_WIDTH, ThreeDimensionalObjectBreederTask.SHAPE_HEIGHT, ThreeDimensionalObjectBreederTask.SHAPE_DEPTH, null, ArrayUtil.doubleOnes(numCPPNInputs()));
		// Get image from multiple angles
		Color evolvedColor = new Color(pair.t2.get(INDEX_RED).floatValue(),pair.t2.get(INDEX_GREEN).floatValue(),pair.t2.get(INDEX_BLUE).floatValue()); // Evolved background color
		double pitch = pair.t2.get(INDEX_PITCH) * 2 * Math.PI; 
		double heading = pair.t2.get(INDEX_HEADING) * 2 * Math.PI;
		BufferedImage[] images = ThreeDimensionalUtil.imagesFromTriangles(tris, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT, 0, numImageSamples, heading, pitch, evolvedColor, vertical);
		ArrayList<INDArray> scoresFromAngles = new ArrayList<>(images.length);
		for(int i = 0; i < images.length; i++) {
			INDArray imageArray = ImageNetClassification.bufferedImageToINDArray(images[i]);
			INDArray scores = ImageNetClassification.getImageNetPredictions(imageArray, PREPROCESS);
			scoresFromAngles.add(scores);
		}
		// Compute average (Make a util method)
		INDArray scores = scoresFromAngles.get(0);
		for(int i = 1; i < scoresFromAngles.size(); i++) {
			scores.add(scoresFromAngles.get(i)); // sum
		}
		scores.div(scoresFromAngles.size()); // divide to get average
		
		ArrayList<Double> behaviorVector = ArrayUtil.doubleVectorFromINDArray(scores);
		Score<Pair<TWEANN, ArrayList<Double>>> result = new Score<>(individual, new double[]{}, behaviorVector);

		if(CommonConstants.watch) {
			// Prints top 4 labels
			String decodedLabels = ImageNetClassification.getImageNetLabelsInstance().decodePredictions(scores);
			System.out.println(decodedLabels);
			for(int i = 0; i < images.length; i++) {
				DrawingPanel picture = GraphicsUtil.drawImage(images[i], "Image", ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT);
				// Wait for user
				MiscUtil.waitForReadStringAndEnterKeyPress();
				picture.dispose();
			}
		}
		if(CommonConstants.netio) {
			// Lot of duplication of computation from Archive. Can that be fixed?
			@SuppressWarnings("unchecked")
			Archive<Pair<TWEANN, ArrayList<Double>>> archive = ((MAPElites<Pair<TWEANN, ArrayList<Double>>>) MMNEAT.ea).getArchive();
			List<String> binLabels = archive.getBinMapping().binLabels();
			for(int i = 0; i < binLabels.size(); i++) {
				Score<Pair<TWEANN, ArrayList<Double>>> elite = archive.getElite(i);
				// If the bin is empty, or the candidate is better than the elite for that bin's score
				double binScore = result.behaviorIndexScore(i);
				if(elite == null || binScore > elite.behaviorIndexScore(i)) {
					if(binScore > pictureInnovationSaveThreshold) {
						String fileName = String.format("%7.5f", binScore) + binLabels.get(i) + individual.getId() + ".gif";						
						String archivePath = archive.getArchiveDirectory();
						File archiveDir = new File(archivePath);
						if(!archiveDir.exists()) archiveDir.mkdir();
						String binPath = archive.getArchiveDirectory() + File.separator + binLabels.get(i);
						File bin = new File(binPath);
						if(!bin.exists()) bin.mkdir();
						String fullName = binPath + File.separator + fileName;
						System.out.println(fullName);						
						// Have to get the full animation sequence, not just three selected frames
						BufferedImage[] allImages = ThreeDimensionalUtil.imagesFromTriangles(tris, ImageNetClassification.IMAGE_NET_INPUT_WIDTH, ImageNetClassification.IMAGE_NET_INPUT_HEIGHT, 0, (int) (AnimationUtil.FRAMES_PER_SEC * 3), heading, pitch, evolvedColor, vertical);
						try {
							AnimationUtil.createGif(allImages, Parameters.parameters.integerParameter("defaultFramePause"), fullName);
						} catch (IOException e) {
							System.out.println("Failed to save gif: " + fullName);
							e.printStackTrace();
							System.exit(1);
						}	
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
		System.out.println("Save gifs of all final elites");
		int saveWidth = Parameters.parameters.integerParameter("imageWidth"); 
		int saveHeight = Parameters.parameters.integerParameter("imageHeight");
		// Save a collection of only the final images from each MAP Elites bin
		if(CommonConstants.netio) {
			@SuppressWarnings("unchecked")
			Archive<Pair<TWEANN, ArrayList<Double>>> archive = ((MAPElites<Pair<TWEANN, ArrayList<Double>>>) MMNEAT.ea).getArchive();
			String finalArchive = archive.getArchiveDirectory() + "Final";
			new File(finalArchive).mkdir(); // Make different directory
			List<String> binLabels = archive.getBinMapping().binLabels();
			for(int i = 0; i < binLabels.size(); i++) {
				String label = binLabels.get(i);
				Score<Pair<TWEANN, ArrayList<Double>>> score = archive.getElite(i);
				Pair<TWEANN,ArrayList<Double>> pair = score.individual.getPhenotype();
				Network cppn = pair.t1;
				
				// Get the shape
				List<Triangle> tris = ThreeDimensionalUtil.trianglesFromCPPN(cppn, saveWidth, saveHeight, ThreeDimensionalObjectBreederTask.CUBE_SIDE_LENGTH, ThreeDimensionalObjectBreederTask.SHAPE_WIDTH, ThreeDimensionalObjectBreederTask.SHAPE_HEIGHT, ThreeDimensionalObjectBreederTask.SHAPE_DEPTH, null, ArrayUtil.doubleOnes(numCPPNInputs()));
				// Render and rotate
				Color evolvedColor = new Color(pair.t2.get(INDEX_RED).floatValue(),pair.t2.get(INDEX_GREEN).floatValue(),pair.t2.get(INDEX_BLUE).floatValue()); // Evolved background color
				double pitch = pair.t2.get(INDEX_PITCH) * 2 * Math.PI; 
				double heading = pair.t2.get(INDEX_HEADING) * 2 * Math.PI;
				BufferedImage[] images = ThreeDimensionalUtil.imagesFromTriangles(tris, saveWidth, saveHeight, 0, (int) (AnimationUtil.FRAMES_PER_SEC * 3), heading, pitch, evolvedColor, vertical);
				
				double binScore = score.behaviorIndexScore(i);
				String fileName = String.format("%7.5f", binScore) + label + ".gif";
				String fullName = finalArchive + File.separator + fileName;
				// Save gif to fullName
				try {
					AnimationUtil.createGif(images, Parameters.parameters.integerParameter("defaultFramePause"), fullName);
				} catch (IOException e) {
					System.out.println("Failed to save gif: " + fullName);
					e.printStackTrace();
					System.exit(1);
				}				
			}
		}
	}


	public int numCPPNInputs() {
		return ThreeDimensionalObjectBreederTask.CPPN_NUM_INPUTS;
	}

	public int numCPPNOutputs() {
		return ThreeDimensionalObjectBreederTask.CPPN_NUM_OUTPUTS;
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main(new String[]{"runNumber:0","randomSeed:0","base:innovationshapes","mu:400","maxGens:2000000",
				"io:true","netio:true","mating:true","task:edu.southwestern.tasks.innovationengines.ShapeInnovationTask",
				"log:InnovationShapes-ResNet","saveTo:ResNet","allowMultipleFunctions:true","ftype:0","netChangeActivationRate:0.3",
				"cleanFrequency:400","recurrency:false","logTWEANNData:false","logMutationAndLineage:true",
				"ea:edu.southwestern.evolution.mapelites.MAPElites",
				"watch:false",
				"experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment",
				"mapElitesBinLabels:edu.southwestern.tasks.innovationengines.ImageNetBinMapping","fs:true",
				//"imageNetModel:edu.southwestern.networks.dl4j.VGG19Wrapper",
				//"imageNetModel:edu.southwestern.networks.dl4j.VGG16Wrapper",
				//"imageNetModel:edu.southwestern.networks.dl4j.GoogLeNetWrapper",
				"imageNetModel:edu.southwestern.networks.dl4j.ResNet50Wrapper",
				//"imageNetModel:edu.southwestern.networks.dl4j.AverageAllZooModelImageNetModels",
				//"imageNetModel:edu.southwestern.networks.dl4j.MinAllZooModelImageNetModels",
				"genotype:edu.southwestern.evolution.genotypes.ShapeInnovationGenotype",
				"pictureInnovationSaveThreshold:0.3",
				"imageWidth:500","imageHeight:500", // Final save size
				"includeFullSigmoidFunction:true", // In original Innovation Engine
				"includeTanhFunction:false",
				"includeIdFunction:false",
				"includeFullApproxFunction:false",
				"includeApproxFunction:false",
				"includeFullGaussFunction:true", // In original Innovation Engine
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

	@Override
	public double[] getUpperBounds() {
		return upper;
	}

	@Override
	public double[] getLowerBounds() {
		return lower;
	}
}
