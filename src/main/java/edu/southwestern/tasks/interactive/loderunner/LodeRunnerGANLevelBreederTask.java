package edu.southwestern.tasks.interactive.loderunner;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.SwingUtilities;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import icecreamyou.LodeRunner.LodeRunner;

/**
 * Interactively evolves Lode Runner levels from the latent space of a GAN network.
 * @author kdste
 *
 */
public class LodeRunnerGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{

	/**
	 * Constructor for the Level Breeder for interactive evolving 
	 * @throws IllegalAccessException
	 */
	public LodeRunnerGANLevelBreederTask() throws IllegalAccessException {
		super();
	}

	/**
	 * Sets the GAN to the Lode Runner type 
	 */
	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
	}

	/**
	 * The label for the window to specify that they are levels from the Lode Runner GAN Model 
	 * @return The label for the window 
	 */
	@Override
	public String getGANModelParameterName() {
		return "LodeRunnerGANModel";
	}

	/**
	 * Gets a level from a the random latent vector 
	 * @return A single level 
	 */
	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(latentVector);
	}

	/**
	 * This method is the kick-off method that calls the static method below 
	 * @return
	 */
	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		return staticResetAndReLaunchGAN(model);
	}

	/**
	 * This method allows users to pick which model that they want to use in the Level breeder 
	 * @param model The name of the file holding the model 
	 * @return 
	 */
	public static Pair<Integer, Integer> staticResetAndReLaunchGAN(String model) {
		int standardSize = GANProcess.latentVectorLength(); //gets the length of the current GANProcess 
		int updatedSize; //to hold the variable if the size of the latent vector changes
		//if we are using the 6 tile mapping, it sets it to the default model, otherwise it updates the size of the latent vector of the new model 
		if(!(Parameters.parameters.booleanParameter("lodeRunnerDistinguishesSolidAndDiggableGround")) && model.equals("LodeRunnerEpochFirstFiveOneGround10000_20_6.pth")) {
			Parameters.parameters.setInteger("GANInputSize", standardSize); // Default latent vector size
			Parameters.parameters.setBoolean("lodeRunnerDistinguishesSolidAndDiggableGround", false);
		}
		else{
			String latentVectorSize = model.substring(model.indexOf("_")+1, model.lastIndexOf("_"));
			updatedSize = Integer.parseInt(latentVectorSize);
			Parameters.parameters.setInteger("GANInputSize", updatedSize); //updates latent vector size for the GANProcess if it has changed 
			Parameters.parameters.setBoolean("lodeRunnerDistinguishesSolidAndDiggableGround", true);
		}
		GANProcess.terminateGANProcess();
		updatedSize = GANProcess.latentVectorLength(); // new model latent vector length 
		return new Pair<>(standardSize, updatedSize);
	}

	/**
	 * Get the directory that holds the GAN models for Lode Runner 
	 * @return File path as a string 
	 */
	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"LodeRunnerGAN";
	}

	/**
	 * Allows users to play the levels in the level breeder with the IceCreamYou code to play lode runner  
	 */
	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		//TODO: This should add a file to the game and allow the player to play it. 
		//probably need a few helper methods, one to save to the right place, maybe we need to add a class/method that defaults to the level we pick from the 
		//level breeder instead of the first level of the campaign that IceCreamYou has by default.  
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		
		new Runnable() {
			public void run() {
				new LodeRunner(level);
			}
		};
	}
	
	private static List<List<Integer>> addSpawn(List<List<Integer>> level, Random rand) {
		
		return level;
		
	}

	/**
	 * Gets the title of the window 
	 * @return Title of the window as a string
	 */
	@Override
	protected String getWindowTitle() {
		return "LodeRunnerGANLevelBreeder";
	}

	/**
	 * Generates new levels to be put on the buttons in the level breeder 
	 * @return BufferedImage of a generated level 
	 */
	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		BufferedImage[] images;
		//sets the height and width for the rendered level to be placed on the button 
		int width1 = LodeRunnerRenderUtil.RENDERED_IMAGE_WIDTH;
		int height1 = LodeRunnerRenderUtil.RENDERED_IMAGE_HEIGHT;
		BufferedImage image = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
		try {
			//if we are using the mapping with 7 tiles, other wise use 6 tiles 
			// ACTUALLY: We can have extra unused tiles in the image array. Easier to have one method that keeps them all around
			//			if(Parameters.parameters.booleanParameter("lodeRunnerDistinguishesSolidAndDiggableGround")){
			images = LodeRunnerRenderUtil.loadImagesNoSpawnTwoGround(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //7 different tiles to display 
			//			}
			//			else {
			//				images = LodeRunnerRenderUtil.loadImagesNoSpawn(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH); //6 different tiles to display 
			//			}
			image = LodeRunnerRenderUtil.createBufferedImage(level,width1,height1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Launches the level breeder, sets GAN input size to 20
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:false","lodeRunnerDistinguishesSolidAndDiggableGround:false","GANInputSize:"+LodeRunnerGANUtil.LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
