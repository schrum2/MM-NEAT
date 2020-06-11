package edu.southwestern.tasks.loderunner;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.random.RandomNumbers;

public class LodeRunnerGANLevelSequenceTask<T> extends LodeRunnerLevelSequenceTask<T> {

	public LodeRunnerGANLevelSequenceTask() {
		super();
	}


	@SuppressWarnings("null")
	@Override
	/**
	 * Gets the sequence of levels to be evolved 
	 * @return An array of levels 
	 */
	public List<List<Integer>>[] getLevelSequence(Genotype<T> individual, int numOfLevels) {
		List<List<Integer>>[] levelSequence = null;
		for(int i = 0; i < numOfLevels; i++) {
			List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
			levelSequence[i] = level;
		}
		return levelSequence;
	}

	/**
	 * Gets a different random seed depending on what level in the sequence it is 
	 * @return The random seed
	 */
	@Override
	public double differentRandomSeedForEveryLevel(int levelInSequence, int lengthOfSequence) {
		//creates random array that is the size of the number of levels in the sequence times the size of the latent vector
		double[] levelSeeds = RandomNumbers.randomArray(lengthOfSequence*10); //10 is the latent vector size 
		//returns the first of each set, for the first level it would return 0, then 10, then 20...etc
		return levelSeeds[levelInSequence*10];
	}
	

}
