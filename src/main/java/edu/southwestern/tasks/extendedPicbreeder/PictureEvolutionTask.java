package edu.southwestern.tasks.extendedPicbreeder;

import java.awt.image.BufferedImage;
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
import edu.southwestern.tasks.BoundedTask;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.tasks.interactive.InteractiveEvolutionTask;
import edu.southwestern.tasks.interactive.picbreeder.PicbreederTask;
import edu.southwestern.tasks.zentangle.ImageFitness;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.DrawingPanel;
import edu.southwestern.util.graphics.GraphicsUtil;

/**
 * Evolve CPPN images randomly to create random Zentangles
 *
 * @author schrum2
 * @param <T>
 *            Phenotype must be a Network (Should be a CPPN)
 */
public class PictureEvolutionTask<T extends Network> extends LonerTask<T> implements NetworkTask, BoundedTask {

	private static final int IMAGE_PLACEMENT = 200;
	public int imageHeight, imageWidth;
	private ImageFitness fitnessFunction = null;
	
	private static double[] lower;
	private static double[] upper;

	/**
	 * Default task constructor
	 */
	public PictureEvolutionTask() {
		try {
			fitnessFunction = (ImageFitness) ClassCreation.createObject("imageFitness");
			MMNEAT.registerFitnessFunction(fitnessFunction.getClass().getName());
		} catch (NoSuchMethodException e) {
			System.out.println("imageFitness not properly defined");
			System.exit(1);
			e.printStackTrace();
		}
	}

	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		
		// TODO: Replace every GraphicsUtil.imageFromCPPN call with PicbreederTask.imageFromCPPN
		
		
		Network n = individual.getPhenotype();
		double[] inputMultiples = ArrayUtil.doubleOnes(n.numInputs());
		if (CommonConstants.watch) {
			BufferedImage child;
			int drawWidth = imageWidth;
			int drawHeight = imageHeight;
			if (Parameters.parameters.booleanParameter("overrideImageSize")) {
				drawWidth = Parameters.parameters.integerParameter("imageWidth");
				drawHeight = Parameters.parameters.integerParameter("imageHeight");
			}
			child = PicbreederTask.imageFromCPPN(n, drawWidth, drawHeight, inputMultiples);
			BufferedImage netPic = InteractiveEvolutionTask.getTWEANNComponent(n).getNetworkImage(drawWidth, drawHeight, false, false);
			// draws picture and network to JFrame
			DrawingPanel childPanel = GraphicsUtil.drawImage(child, "output", drawWidth, drawHeight);
			childPanel.setLocation(IMAGE_PLACEMENT, 0);
			DrawingPanel netPanel = GraphicsUtil.drawImage(netPic, "Network", drawWidth, drawHeight);
			netPanel.setLocation(IMAGE_PLACEMENT, child.getHeight() + 10);
			considerSavingImage(childPanel);
			childPanel.dispose();
			netPanel.dispose();
		}
		// Reduce evaluation time by creating small images
		BufferedImage smallImage = PicbreederTask.imageFromCPPN(n, 50, 50, inputMultiples);
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
		return new String[] { "X-coordinate", "Y-coordinate", "distance from center", "bias"};
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
		int seed = 11; //(int)(Math.random()*100);
		if (args.length == 1) {
			seed = Integer.parseInt(args[0]);
		}
		try {
			MMNEAT.main(new String[] { "runNumber:" + seed, "randomSeed:" + seed, "trials:1", "mu:20", "maxGens:100", 
					"standardPicBreederHSBRestriction:false",
					"base:extendedPicbreeder", "log:ExtendedPicbreeder-RandomEnhanced", "saveTo:RandomEnhanced",
					//"base:extendedPicbreeder", "log:ExtendedPicbreeder-HB3ColorEnhanced", "saveTo:HB3ColorEnhanced",
					//"base:extendedPicbreeder", "log:ExtendedPicbreeder-RandomNoScaleEnhanced", "saveTo:RandomNoScaleEnhanced",
					//"base:extendedPicbreeder", "log:ExtendedPicbreeder-RandomStandard", "saveTo:RandomStandard",
					//"base:extendedPicbreeder", "log:ExtendedPicbreeder-HB3ColorStandard", "saveTo:HB3ColorStandard",
					// Uncomment this to have extended genotypes. May need other parameters used in PicbreederTask too
					"genotype:edu.southwestern.evolution.genotypes.EnhancedCPPNPictureGenotype",
					//"minScale:1.0", "maxScale:1.0", // Uncomment to turn off evolution of scale
					"minScale:1.0", "maxScale:2.0", // Fixed scale range
					"io:true", "netio:true", "mating:true", "fs:false", "starkPicbreeder:false",
					"task:edu.southwestern.tasks.extendedPicbreeder.PictureEvolutionTask", "allowMultipleFunctions:true",
					"ftype:0", "watch:true", "netChangeActivationRate:0.3", "cleanFrequency:-1",
					"simplifiedInteractiveInterface:false", "recurrency:false", "saveAllChampions:true",
					"cleanOldNetworks:false", "ea:edu.southwestern.evolution.nsga2.NSGA2",
					"imageWidth:2000", "imageHeight:2000", "imageSize:200", 
					
					// This is the set of functions set to their original values
					//"includeFullSigmoidFunction:true",
					//"includeFullGaussFunction:true", "includeCosineFunction:true", "includeGaussFunction:false",
					//"includeIdFunction:true", "includeTriangleWaveFunction:false", "includeSquareWaveFunction:false",
					//"includeFullSawtoothFunction:false", "includeSigmoidFunction:false", "includeAbsValFunction:false",
					//"includeSawtoothFunction:false",
					
					"includeFullSigmoidFunction:true",
					"includeFullGaussFunction:true", "includeCosineFunction:true", "includeGaussFunction:false",
					"includeIdFunction:true", "includeTriangleWaveFunction:false", "includeSquareWaveFunction:false",
					"includeFullSawtoothFunction:false", "includeSigmoidFunction:false", "includeAbsValFunction:false",
					"includeSawtoothFunction:false",
					
					"overrideImageSize:true","imageWidth:500","imageHeight:500",
					//"picbreederImageScale:10.0", "picbreederImageRotation:5.0", // <- Not relevant when EnhancedCPPNPictureGenotype is used
					//"picbreederImageTranslationX:0.0", "picbreederImageTranslationY:0.0"});
					//"imageFitness:edu.southwestern.tasks.zentangle.ColorsImageFitness"});
					"imageFitness:edu.southwestern.tasks.zentangle.ColorRangeAndSumFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.ColorRangeFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.RandomPlusColorRangeFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.RandomImageFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackAndColorsInLightPortionImageFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackAndColorsImageFitness"});
					//"imageFitness:edu.southwestern.tasks.zentangle.HalfBlackImageFitness"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}	
	}

	@Override
	public void postConstructionInitialization() {
		MMNEAT.setNNInputParameters(numInputs(), numOutputs());
	}
	public static double[] getStaticUpperBounds() {
		if(upper == null) upper = new double[] {Parameters.parameters.doubleParameter("maxScale"), Parameters.parameters.booleanParameter("enhancedCPPNCanRotate") ? 2*Math.PI : 0.0, Parameters.parameters.doubleParameter("imageCenterTranslationRange"), Parameters.parameters.doubleParameter("imageCenterTranslationRange")};		
		return upper;
	}

	public static double[] getStaticLowerBounds() {
		if(lower == null) lower = new double[] {Parameters.parameters.doubleParameter("minScale"), 0, -Parameters.parameters.doubleParameter("imageCenterTranslationRange"), -Parameters.parameters.doubleParameter("imageCenterTranslationRange")};
		return lower;
	}

	@Override
	public double[] getUpperBounds() {
		return getStaticUpperBounds();
	}

	public double[] getLowerBounds() {
		return getStaticLowerBounds();
	}
				
}
