package megaManMaker;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.tasks.interactive.InteractiveGANLevelEvolutionTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MegaManGANLevelBreederTask extends InteractiveGANLevelEvolutionTask{

	public MegaManGANLevelBreederTask() throws IllegalAccessException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configureGAN() {
		GANProcess.type = GANProcess.GAN_TYPE.MEGA_MAN;
		
	}

	@Override
	public String getGANModelParameterName() {
		// TODO Auto-generated method stub
		return "MegaManGANModel";
	}

	@Override
	public List<List<Integer>> levelListRepresentation(double[] latentVector) {
		// TODO Auto-generated method stub
		return MegaManGANUtil.generateOneLevelListRepresentationFromGAN(latentVector);
	}

	@Override
	public Pair<Integer, Integer> resetAndReLaunchGAN(String model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGANModelDirectory() {
		return "src"+File.separator+"main"+File.separator+"python"+File.separator+"GAN"+File.separator+"MegaManGAN";
	}

	@Override
	public void playLevel(ArrayList<Double> phenotype) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		int levelNumber = 2020;
		MegaManVGLCUtil.convertMegaManLevelToMMLV(level, levelNumber);
		//save level and play
	}

	@Override
	protected String getWindowTitle() {
		// TODO Auto-generated method stub
		return "MegaManGANLevelBreeder";
	}

	@Override
	protected BufferedImage getButtonImage(ArrayList<Double> phenotype, int width, int height,
			double[] inputMultipliers) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(phenotype);
		List<List<Integer>> level = levelListRepresentation(doubleArray);
		BufferedImage[] images;
		//sets the height and width for the rendered level to be placed on the button 
		int width1 = MegaManRenderUtil.renderedImageWidth(level.get(0).size());
		int height1 = MegaManRenderUtil.renderedImageHeight(level.size());
		BufferedImage image = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
		try {

			images = MegaManRenderUtil.loadImagesForASTAR(MegaManRenderUtil.MEGA_MAN_TILE_PATH); //7 different tiles to display 
			image = MegaManRenderUtil.createBufferedImage(level,width1,height1, images);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	
	/**
	 * Launches the level breeder, sets GAN input size to 5
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:false","GANInputSize:"+MegaManGANUtil.LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:megaManMaker.MegaManGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

}
