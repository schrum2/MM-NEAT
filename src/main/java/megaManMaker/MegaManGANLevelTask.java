package megaManMaker;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.mario.gan.MarioGANUtil;
import edu.southwestern.util.datastructures.ArrayUtil;

public class MegaManGANLevelTask extends MegaManLevelTask<ArrayList<Double>> {
	/**
	 * Extract real-valued latent vector from genotype and then send to GAN to get a MegaMan level
	 */
	@Override
	public ArrayList<List<Integer>> getMegaManLevelListRepresentationFromGenotype(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> latentVector = individual.getPhenotype();
		return getMegaManLevelListRepresentationFromStaticGenotype(latentVector);
	}

	public static ArrayList<List<Integer>> getMegaManLevelListRepresentationFromStaticGenotype(ArrayList<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		ArrayList<List<Integer>> level = MarioGANUtil.generateLevelListRepresentationFromGAN(doubleArray);
		return level;
	}

}
