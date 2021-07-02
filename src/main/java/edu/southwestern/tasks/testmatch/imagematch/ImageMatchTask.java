package edu.southwestern.tasks.testmatch.imagematch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.SinglePopulationGenerationalEA;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.innovationengines.PictureTargetTask;
import edu.southwestern.tasks.testmatch.MatchDataTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Image match training task for a CPPN
 *
 * @author gillespl
 * @param <T>
 *            Phenotype must be a Network (Should be a CPPN)
 */
public class ImageMatchTask<T extends Network> extends MatchDataTask<T> {

	public static final String IMAGE_MATCH_PATH = "data" + File.separator + "imagematch";
	private static final int IMAGE_PLACEMENT = 200;
	private static final int HUE_INDEX = 0;
	private static final int SATURATION_INDEX = 1;
	private static final int BRIGHTNESS_INDEX = 2;
	private static final int NUM_CPPN_OUTPUTS = 3;
	private Network individual;
	private BufferedImage img = null;
	public int imageHeight, imageWidth;
	private double[] targetImageFeatures; 
	
	private double bestFitnessSoFar = Double.NEGATIVE_INFINITY;
	private BufferedImage bestImageSoFar = null;
	private long bestIdSoFar;

	/**
	 * Default task constructor
	 */
	public ImageMatchTask() {
		this(Parameters.parameters.stringParameter("matchImageFile"));
		MatchDataTask.pauseForEachCase = false;
	}

	/**
	 * Constructor for ImageMatchTask
	 *
	 * @param filename name of file pathway for image
	 */
	public ImageMatchTask(String filename) {
		try {// throws and exception if filename is not valid
			img = ImageIO.read(new File(IMAGE_MATCH_PATH + File.separator + filename));
		} catch (IOException e) {
			System.out.println("Could not load image: " + filename);
			System.exit(1);
		}
		imageHeight = img.getHeight();
		imageWidth = img.getWidth();
		
		targetImageFeatures = GraphicsUtil.flatFeatureArrayFromBufferedImage(img);
	}

	/**
	 * Evaluate method. If watch is true, puts the image and network into a
	 * JFrame with option to save both as a bmp. Else, the super evaluate method
	 * is called.
	 *
	 * @return MSE comparison between image and CPPN outputs
	 */
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		double[] candidateFeatures = null;
		BufferedImage child = null;
		if (CommonConstants.watch || Parameters.parameters.booleanParameter("useWoolleyImageMatchFitness") || Parameters.parameters.booleanParameter("useRMSEImageMatchFitness")) {
			Network n = individual.getPhenotype();
			int drawWidth = imageWidth;
			int drawHeight = imageHeight;
			if (Parameters.parameters.booleanParameter("overrideImageSize")) {
				drawWidth = Parameters.parameters.integerParameter("imageWidth");
				drawHeight = Parameters.parameters.integerParameter("imageHeight");
			}
			child = GraphicsUtil.imageFromCPPN(n, drawWidth, drawHeight);
			candidateFeatures = GraphicsUtil.flatFeatureArrayFromBufferedImage(child);
			
			if (CommonConstants.watch) {
				// draws picture and network to JFrame
				DrawingPanel parentPanel = GraphicsUtil.drawImage(img, "target", drawWidth, drawHeight);
				DrawingPanel childPanel = GraphicsUtil.drawImage(child, "output", drawWidth, drawHeight);
				childPanel.setLocation((img.getWidth() + IMAGE_PLACEMENT), 0);
				considerSavingImage(childPanel);
				parentPanel.dispose();
				childPanel.dispose();
			}			
		}
		// Too many outputs to print to console. Don't want to watch.
		boolean temp = CommonConstants.watch;
		CommonConstants.watch = false; // Prevent watching of console showing error energy
		Score<T> result = null;
		
		if(Parameters.parameters.booleanParameter("useWoolleyImageMatchFitness")) {
			double error = PictureTargetTask.candidateVsTargetError(candidateFeatures, targetImageFeatures);
			double fitness = 1 - error * error;
			double[] resultScore = new double[]{fitness};
			result = new Score<>(individual, resultScore);
		} else if(Parameters.parameters.booleanParameter("useRMSEImageMatchFitness")) {
			double fitness = PictureTargetTask.rootMeanSquareErrorFitness(candidateFeatures, targetImageFeatures);
			double[] resultScore = new double[]{fitness};
			result = new Score<>(individual, resultScore);
		} else {
			result = super.evaluate(individual);// if watch=false
		}
		
//		if(Parameters.parameters.booleanParameter("useWoolleyImageMatchFitness")) {
//			double error = candidateVsTargetError(candidateFeatures, targetImageFeatures);
//			return 1 - error * error;
//		} else if (Parameters.parameters.booleanParameter("useRMSEImageMatchFitness")) {
//			return rootMeanSquareErrorFitness(candidateFeatures, targetImageFeatures);
//		} else {
//			throw new IllegalStateException("Proper fitness function for PictureTargetTask not specified");
//		}
//		
		
		CommonConstants.watch = temp;
		this.individual = individual.getPhenotype();
		result.giveBehaviorVector(getBehaviorVector());
		
		// TODO: If fitness better than bestFitnessSoFar then save image
		double fitness = result.scores[0];
		if(child != null && fitness > bestFitnessSoFar) {
			bestImageSoFar = child;
			bestFitnessSoFar = fitness;
			bestIdSoFar = individual.getId();
		}
		
		return result;
	}
	
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		ArrayList<Score<T>> result = super.evaluateAll(population);
		if(CommonConstants.netio && bestImageSoFar != null) {
			@SuppressWarnings("rawtypes")
			int gen = ((SinglePopulationGenerationalEA) MMNEAT.ea).currentGeneration();
			String filename1 = FileUtilities.getSaveDirectory() + File.separator + "gen" +gen+"fitness" + bestFitnessSoFar + "championId"+bestIdSoFar+".jpg";
			GraphicsUtil.saveImage(bestImageSoFar, filename1);
			System.out.println("save directory is: " + FileUtilities.getSaveDirectory());
			System.out.println("image " + filename1 + " was saved successfully. Size: "+bestImageSoFar.getWidth()+" by "+bestImageSoFar.getHeight());
			bestImageSoFar = null; // Set null after saving so we don't re-save the same over and over
		}
		return result;
	}

	/**
	 * Allows image and network to be saved as a bmp if user chooses to do so
	 *
	 * @param panel the drawing panel containing the image to be saved
	 */
	public static void considerSavingImage(DrawingPanel panel) {
		System.out.println("Save image? y/n");
		String input = MiscUtil.CONSOLE.next();
		if (input.equals("y")) {
			System.out.println("enter filename");
			String filename = MiscUtil.CONSOLE.next();
                        // allows user to choose a unique name for 
                        // file and to not worry about adding '.bmp'
			panel.save(filename + ".bmp");
		}
	}

	/**
	 * Returns labels for input
	 *
	 * @return List of CPPN outputs
	 */
	@Override
	public String[] sensorLabels() {
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias" };
	}

	/**
	 * Returns labels for output
	 *
	 * @return list of CPPN outputs
	 */
	@Override
	public String[] outputLabels() {
		return new String[] { "hue-value", "saturation-value", "brightness-value" };
	}

	/**
	 * Returns number of inputs to network
	 *
	 * @return Number of inputs: X, Y, distance from center, Bias
	 */
	@Override
	public int numInputs() {
		return 4;
	}

	/**
	 * Returns number of outputs from network
	 *
	 * @return Number of outputs: Hue, Saturation, and Brightness
	 */
	@Override
	public int numOutputs() {
		return NUM_CPPN_OUTPUTS;
	}

	/**
	 * Gets the expected input and output from picture for network to compare
	 * against
	 *
	 * @return an ArrayList of all the pairs of expected inputs and outputs
	 */
	@Override
	public ArrayList<Pair<double[], double[]>> getTrainingPairs() {
		return getImageMatchTrainingPairs(img);
	}

	/**
	 * Gets the expected input and output from picture for network to compare
	 * against.  Specifically gathering training data about that image.  The
	 * inputs correspond to the coordinates and the outputs correspond to 
	 * a specific color (determined by hue, saturation and brightness).
	 * 
	 * @param img the image being evaluated
	 * @return returns the 2D array of pairs (inputs and outputs)
	 */
	public static ArrayList<Pair<double[], double[]>> getImageMatchTrainingPairs(BufferedImage img) {
		ArrayList<Pair<double[], double[]>> pairs = new ArrayList<Pair<double[], double[]>>();	// creating a new 2D ArrayList of doubles
		// looks at each pixel in the image
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color color = new Color(img.getRGB(x, y));	// Find the rgb color at the coordinates x and y and store them in a new color
				// 
				float[] hsb = new float[NUM_CPPN_OUTPUTS];
				// Converting the color to the corresponding hue, saturation, and brightness indeces
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				// -1 input is used as default so that time is not taken into account
				// Add the coordinates and the corresponding color into the 2D array
				pairs.add(new Pair<double[], double[]>(GraphicsUtil.get2DObjectCPPNInputs(x, y, img.getWidth(), img.getHeight(), -1),
						new double[] { hsb[HUE_INDEX], hsb[SATURATION_INDEX], hsb[BRIGHTNESS_INDEX] }));
			}
		}
		return pairs;	// return the 2D ArrayList containing all of the pairs
	}

	/**
	 * gets behavior vector for behavioral diversity algorithm
     * @return The H, S, and B values for each pixel in the image
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		ArrayList<Double> results = new ArrayList<Double>(img.getHeight() * img.getWidth());
		BufferedImage child = GraphicsUtil.imageFromCPPN(individual, img.getWidth(), img.getHeight());
		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				Color color = new Color(child.getRGB(i, j));
				float[] hsb = new float[numOutputs()];
				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
				for (int k = 0; k < hsb.length; k++) {
					results.add((double) hsb[k]);
				}
			}
		}

		return results;
	}

	/**
	 * main method 
	 *
	 * @param args
	 * @throws NoSuchMethodException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		
		
		// For test runs
		MMNEAT.main(new String[]{
				"runNumber:0","randomSeed:0", "base:imagematch", "trials:1", "maxGens:1000", "mu:100", "io:true", "netio:true", 
				"mating:true", "fs:false", "task:edu.southwestern.tasks.testmatch.imagematch.ImageMatchTask", 
				"log:ImageMatch-RMSE", 
				"saveTo:RMSE", 
				"allowMultipleFunctions:true", "ftype:0", "watch:false", "netChangeActivationRate:0.3", "overrideImageSize:false", 
				"imageHeight:200", "imageWidth:300", "saveAllChampions:true",
				"useWoolleyImageMatchFitness:false", "useRMSEImageMatchFitness:true", // Pick one or none
				//"matchImageFile:TexasFlag.png",
				//"matchImageFile:cat.jpg",
				"matchImageFile:failedskull.jpg",
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
				"includeSquareWaveFunction:false", "blackAndWhitePicbreeder:true"}); 
		
		
		// used to create a random CPPN image. WHY?
		
//		MMNEAT.clearClasses();
//		EvolutionaryHistory.setInnovation(0);
//		EvolutionaryHistory.setHighestGenotypeId(0);
//		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "allowMultipleFunctions:true", "netChangeActivationRate:0.4", "recurrency:false", "useWoolleyImageMatchFitness:false", "useRMSEImageMatchFitness:false"});
//		MMNEAT.loadClasses();
//		randomCPPNimage(true, 200, 200, 200);
	}

	public static void draw8RandomImages() {
		randomCPPNimage(true, 400);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				randomCPPNimage(false, i * 300, j * 300, 100);
			}
		}
	}

	public static DrawingPanel randomCPPNimage(boolean offerToSave, int size) {
		return randomCPPNimage(offerToSave, 0, 0, size);
	}

	public static void testImageConsistency() {
		final int NUM_MUTATIONS = 200;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for (int i = 0; i < NUM_MUTATIONS; i++) {
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
		int SMALL = 100;
		BufferedImage small = GraphicsUtil.imageFromCPPN(n, SMALL, SMALL);
		DrawingPanel smallPanel = GraphicsUtil.drawImage(small, "small", SMALL, SMALL);
		smallPanel.setLocation(0, 0);
		int MEDIUM = 300;
		BufferedImage medium = GraphicsUtil.imageFromCPPN(n, MEDIUM, MEDIUM);
		DrawingPanel mediumPanel = GraphicsUtil.drawImage(medium, "medium", MEDIUM, MEDIUM);
		mediumPanel.setLocation(SMALL, 0);

		int LARGE = 600;
		BufferedImage large = GraphicsUtil.imageFromCPPN(n, LARGE, LARGE);
		DrawingPanel largePanel = GraphicsUtil.drawImage(large, "large", LARGE, LARGE);
		largePanel.setLocation(SMALL + MEDIUM, 0);
	}

	/**
	 * Creates a random image of given size and numMutations and puts it in
	 * JFrame with option to save image and network as a bmp
	 *
	 * @param offerToSave
	 *            Whether to pause and ask to save image
	 * @param x
	 *            x-coordinate to place image window
	 * @param y
	 *            y-coordinate to place image window
	 * @return panel on which image was drawn
	 */
	public static DrawingPanel randomCPPNimage(boolean offerToSave, int x, int y, int size) {

		final int NUM_MUTATIONS = 200;

		TWEANNGenotype toDraw = new TWEANNGenotype(4, 3, false, 0, 1, 0);
		for (int i = 0; i < NUM_MUTATIONS; i++) {
			toDraw.mutate();
		}
		TWEANN n = toDraw.getPhenotype();
		BufferedImage child = GraphicsUtil.imageFromCPPN(n, size, size);

		System.out.println(n.toString());

		DrawingPanel childPanel = GraphicsUtil.drawImage(child, "output", size, size);
		childPanel.setLocation(x, y);

		if (offerToSave) {
			DrawingPanel network = new DrawingPanel(size, size, "network");
			n.draw(network);
			network.setLocation(300, 0);
			Scanner scan = MiscUtil.CONSOLE;
			System.out.println("would you like to save this image? y/n");
			if (scan.next().equals("y")) {
				System.out.println("enter filename");
				String filename = scan.next();
				childPanel.save(filename + ".bmp");
				System.out.println("save network? y/n");
				if (scan.next().equals("y")) {
					network.save(filename + "network.bmp");
				}
			}
			network.dispose();
			childPanel.dispose();
			System.exit(0);
		}
		return childPanel;
	}
}
