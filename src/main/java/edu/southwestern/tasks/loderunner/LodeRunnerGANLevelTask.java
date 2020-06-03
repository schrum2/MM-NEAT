package edu.southwestern.tasks.loderunner;


import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.ArrayUtil;


public class LodeRunnerGANLevelTask extends LodeRunnerLevelTask<List<Double>> {

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0","randomSeed:1","bigInteractiveButtons:false","lodeRunnerDistinguishesSolidAndDiggableGround:false","GANInputSize:"+LodeRunnerGANUtil.LATENT_VECTOR_SIZE,"showKLOptions:false","trials:1","mu:16","maxGens:500","io:false","netio:false","mating:true","fs:false","task:edu.southwestern.tasks.interactive.loderunner.LodeRunnerGANLevelBreederTask","watch:true","cleanFrequency:-1","genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype","simplifiedInteractiveInterface:false","saveAllChampions:true","ea:edu.southwestern.evolution.selectiveBreeding.SelectiveBreedingEA","imageWidth:2000","imageHeight:2000","imageSize:200"});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(
			Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getLodeRunnerLevelListRepresentationFromGenotypeStatic(latentVector);
	}

	private List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotypeStatic(List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(doubleArray);
 		return level;
	}

}
