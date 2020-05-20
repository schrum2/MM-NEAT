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

public class LodeRunnerGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{
	public static final int LATENT_VECTOR_SIZE = 20;//latent vector dimension, 20 improved the model a lot

	public LodeRunnerGANLevelBreederTask() throws IllegalAccessException {
		super();
		
	}

	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.LODE_RUNNER;
	}

	@Override
	public String getGANModelParameterName() {
		return "LodeRunnerGANModel";
	}

	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		return LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(latentVector);
	}

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		//TODO
		return null;
	}

	@Override
	public String getGANModelDirectory() {
		return "python"+File.separator+"GAN"+File.separator+"LodeRunnerGAN";
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		//TODO
	}

	@Override
	protected String getWindowTitle() {
		return "LodeRunnerGANLevelBreeder";
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(doubleArray);
		BufferedImage[] images;
		int width1 = LodeRunnerRenderUtil.LODE_RUNNER_TILE_X*LodeRunnerRenderUtil.LODE_RUNNER_COLUMNS;
		int height1 = LodeRunnerRenderUtil.LODE_RUNNER_TILE_Y*LodeRunnerRenderUtil.LODE_RUNNER_ROWS;
		BufferedImage image = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
		try {
			images = LodeRunnerRenderUtil.loadImagesNoSpawn(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH);
			image = LodeRunnerRenderUtil.getBufferedImage(level, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:true","GANInputSize:"+LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
