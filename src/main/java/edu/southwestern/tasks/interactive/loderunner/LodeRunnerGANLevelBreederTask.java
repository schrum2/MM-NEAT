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
		BufferedImage image = new BufferedImage(0, 0, 0);
		try {
			images = LodeRunnerRenderUtil.loadImages(LodeRunnerRenderUtil.LODE_RUNNER_TILE_PATH);
			image = LodeRunnerRenderUtil.getBufferedImage(level, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
