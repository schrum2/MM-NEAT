package megaManMaker;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.datastructures.ArrayUtil;

public class MegaManGANLevelTask extends MegaManLevelTask<List<Double>> {
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public List<List<Integer>> getMegaManLevelListRepresentationFromGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessUp, GANProcess ganProcessDown, Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(ganProcessHorizontal, ganProcessUp, ganProcessDown, latentVector);
	}

	public static List<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(GANProcess ganProcessHorizontal, GANProcess ganProcessUp, GANProcess ganProcessDown,List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = MegaManGANUtil.generateOneLevelListRepresentationFromGANVerticalAndHorizontal(ganProcessHorizontal, ganProcessUp,ganProcessDown,doubleArray);
		return level;
	}

}
