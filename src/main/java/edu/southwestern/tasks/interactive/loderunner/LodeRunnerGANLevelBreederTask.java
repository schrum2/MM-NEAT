package edu.southwestern.tasks.interactive.loderunner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.loderunner.LodeRunnerGANUtil;
import edu.southwestern.tasks.loderunner.LodeRunnerRenderUtil;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

/**
 * Interactively evolve Lode Runner levels from the latent space of a GAN network.
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

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		//TODO
		return null;
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
		//TODO
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
		//sets the height and width of the 
		int width1 = LodeRunnerRenderUtil.RENDERED_IMAGE_WIDTH;
		int height1 = LodeRunnerRenderUtil.RENDERED_IMAGE_HEIGHT;
		BufferedImage image = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
		try {
			images = LodeRunnerRenderUtil.loadImagesNoSpawn(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH);
			image = LodeRunnerRenderUtil.createBufferedImage(level,width1,height1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * Launches the level breeder sets GAN input size to 20
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:true","GANInputSize:"+LodeRunnerGANUtil.LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
