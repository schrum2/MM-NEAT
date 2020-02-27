package edu.southwestern.tasks.zentangle;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.evolution.SinglePopulationGenerationalEA;

/**
 * Evolve CPPN images randomly to create random Zentangles
 *
 * @author schrum2
 * @param <T>
 *            Phenotype must be a Network (Should be a CPPN)
 */
public class ZentangleTask<T extends Network> extends LonerTask<T> implements NetworkTask {

	private static final int IMAGE_PLACEMENT = 200;
	private static final int ZENTANGLES_PER_GENERATION = 3; // Make command line param?
	public int imageHeight, imageWidth;
	private ImageFitness fitnessFunction = null;

	/**
	 * Default task constructor
	 */
	public ZentangleTask() {
		try {
			fitnessFunction = (ImageFitness) ClassCreation.createObject("imageFitness");
		} catch (NoSuchMethodException e) {
			System.out.println("imageFitness not properly defined");
			System.exit(1);
			e.printStackTrace();
		}
	}

	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		ArrayList<Score<T>> result = super.evaluateAll(population);
		// Population finished being evaluated, so make a Zentangle
		@SuppressWarnings("unchecked")
		String directory = FileUtilities.getSaveDirectory() + "/gen"+((SinglePopulationGenerationalEA<T>) MMNEAT.ea).currentGeneration();
		if(new File(directory).exists()) { // Made by parents ... we are in child group
			directory += "/children";
		} else { // New directory? Then this is parent population
			directory += "/parents";
		}
		double[] inputMultipliers = new double[this.numInputs()];
		Arrays.fill(inputMultipliers, 1); // All inputs turned on

		
		for(int j = 0; j < ZENTANGLES_PER_GENERATION; j++) {
			// Add some random tiles to make a zentangle with
			// Need at least two tiles
			int howMany = RandomNumbers.randomGenerator.nextInt(5)+2;
			ArrayList<Genotype<T>> randomGenotypes = RandomNumbers.randomChoose(population, howMany);
			ArrayList<T> chosenTiles = new ArrayList<T>(howMany);
			// Extract the phenotypes
			for(int i = 0; i < howMany; i++) {
				chosenTiles.add(randomGenotypes.get(i).getPhenotype());
			}
			// Create Zentangle!
			PicbreederTask.zentangle(directory+"/example"+j, chosenTiles, inputMultipliers);			
		}
		// Pause to look at Zentangle
		// Replace this with command line option later.
		//MiscUtil.waitForReadStringAndEnterKeyPress();
		
		// Return evaluated population
		return result;
	}
	
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		Network n = individual.getPhenotype();
		if (CommonConstants.watch) {
			BufferedImage child;
			int drawWidth = imageWidth;
			int drawHeight = imageHeight;
			if (Parameters.parameters.booleanParameter("overrideImageSize")) {
				drawWidth = Parameters.parameters.integerParameter("imageWidth");
				drawHeight = Parameters.parameters.integerParameter("imageHeight");
			}
			child = GraphicsUtil.imageFromCPPN(n, drawWidth, drawHeight);
			// draws picture and network to JFrame
			DrawingPanel childPanel = GraphicsUtil.drawImage(child, "output", drawWidth, drawHeight);
			childPanel.setLocation(IMAGE_PLACEMENT, 0);
			considerSavingImage(childPanel);
			childPanel.dispose();
		}
		// Reduce evaluation time by creating small images
		BufferedImage smallImage = GraphicsUtil.imageFromCPPN(n, 50, 50);
		// Random fitness score
		Score<T> result = new Score<>(individual, fitness(smallImage), getBehaviorVector());
		if(CommonConstants.watch) {
			System.out.println("Fitness:" + Arrays.toString(result.scores));
		}
		return result;
	}

	private double[] fitness(BufferedImage smallImage) {
		return fitnessFunction.fitness(smallImage);
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
	public int numInputs() {
		return 4;
	}

	/**
	 * Returns number of outputs from network
	 *
	 * @return Number of outputs: Hue, Saturation, and Brightness
	 */
	public int numOutputs() {
		return 3;
	}

	/**
	 * gets behavior vector for behavioral diversity algorithm
     * @return The H, S, and B values for each pixel in the image
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		// Disable for now
		return null;
		
//		ArrayList<Double> results = new ArrayList<Double>(img.getHeight() * img.getWidth());
//		BufferedImage child = GraphicsUtil.imageFromCPPN(individual, img.getWidth(), img.getHeight());
//		for (int i = 0; i < img.getWidth(); i++) {
//			for (int j = 0; j < img.getHeight(); j++) {
//				Color color = new Color(child.getRGB(i, j));
//				float[] hsb = new float[numOutputs()];
//				Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
//				for (int k = 0; k < hsb.length; k++) {
//					results.add((double) hsb[k]);
//				}
//			}
//		}
//		return results;
	}

	@Override
	public int numObjectives() {
		return fitnessFunction.numberObjectives();
	}

	@Override
	public double getTimeStamp() {
		// Not used
		return -1;
	}
	
	public static void main(String[] args) {
		// args[0] is the random seed
		int seed = (int)(Math.random()*100);
		if (args.length == 1) {
			seed = Integer.parseInt(args[0]);
		}
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:16", "maxGens:50",
					//"base:zentangles", "log:Zentangles-HalfBlack", "saveTo:HalfBlack",
					"io:false", "netio:false", "mating:true", "fs:false", "starkPicbreeder:true",
					"task:edu.southwestern.tasks.zentangle.ZentangleTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:false", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"simplifiedInteractiveInterface:false", "recurrency:false", "saveAllChampions:true",
					"cleanOldNetworks:false", "ea:edu.southwestern.evolution.nsga2.NSGA2",
					"imageWidth:2000", "imageHeight:2000", "imageSize:200", "includeFullSigmoidFunction:true",
					"includeFullGaussFunction:true", "includeCosineFunction:true", "includeGaussFunction:false",
					"includeIdFunction:true", "includeTriangleWaveFunction:false", "includeSquareWaveFunction:false",
					"includeFullSawtoothFunction:false", "includeSigmoidFunction:false", "includeAbsValFunction:false",
					"includeSawtoothFunction:false","overrideImageSize:true","imageWidth:500","imageHeight:500",
					//"imageFitness:edu.southwestern.tasks.zentangle.RandomImageFitness"});
					"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackAndColorsInLightPortionImageFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackAndColorsImageFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackImageFitness"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}	
	}
				
}
