package edu.southwestern.tasks.loderunner;


import java.io.FileNotFoundException;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * 
 * @author kdste
 *
 */
public class LodeRunnerGANLevelTask extends LodeRunnerLevelTask<List<Double>> {

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[]{"runNumber:0", "randomSeed:0", "base:loderunnerlevels", "log:LodeRunnerLevels-Direct", "saveTo:Direct", "LodeRunnerGANModel:LodeRunnerEpochAllGroundFirstHundred100000_20_7.pth", "watch:true", "GANInputSize:20", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false watch:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Random seed is simply the first latent variable
	 */
	public double getRandomSeedForSpawnPoint(Genotype<List<Double>> individual) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		double firstLatentVariable = doubleArray[0];
		return firstLatentVariable;
	}

	/**
	 * Gets the phenotype vector from the genotype and then returns a level by calling the static method below 
	 * @param List<Double> from the Genotype of the level 
	 * @return List<List<Integer>> that represents a level 
	 */
	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(
			Genotype<List<Double>> individual) {
		List<Double> latentVector = individual.getPhenotype();
		return getLodeRunnerLevelListRepresentationFromGenotypeStatic(latentVector);
	}

	/**
	 * Passes in a latent vector and returns a level from that latent vector 
	 * @param latentVector 
	 * @return A single level, List<List<Integer>>
	 */
	private List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotypeStatic(List<Double> latentVector) {
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		List<List<Integer>> level = LodeRunnerGANUtil.generateOneLevelListRepresentationFromGAN(doubleArray);
 		return level;
	}

}
