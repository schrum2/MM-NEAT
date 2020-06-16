package edu.southwestern.tasks.loderunner;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.random.RandomNumbers;

public class LodeRunnerGANLevelSequenceTask<T> extends LodeRunnerLevelSequenceTask<T> {

	public static void main(String[] args) {
		try {
			MMNEAT.main(new String[] {"runNumber:2", "randomSeed:2", "lodeRunnerLevelSequenceAverages:true","lodeRunnerLevelSequenceIndividual:false", "lodeRunnerAllowsSimpleAStarPath:true", "lodeRunnerAllowsConnectivity:true", "base:loderunnerlevels", "log:LodeRunnerLevels-LevelSequence", "saveTo:LevelSequence", "LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "watch:false", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public LodeRunnerGANLevelSequenceTask() {
		super();
	}


	@SuppressWarnings("null")
	@Override
	/**
	 * Gets the sequence of levels to be evolved 
	 * @return An array of levels 
	 */
	public ArrayList<List<List<Integer>>> getLevelSequence(Genotype<T> individual, int numOfLevels) {
		ArrayList<List<List<Integer>>> levelSequence = new ArrayList<>(numOfLevels);
		for(int i = 0; i < numOfLevels; i++) {
				List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromGenotype(individual);
//				System.out.println("level: "+level);
//				MiscUtil.waitForReadStringAndEnterKeyPress();
				levelSequence.add(level);
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
