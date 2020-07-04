package edu.southwestern.tasks.loderunner;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;

public class LodeRunnerGANLevelSequenceTask extends LodeRunnerLevelSequenceTask<List<Double>> {

	public static void main(String[] args) {
		try {
			//to test average
			//MMNEAT.main(new String[] {"runNumber:2", "randomSeed:2", "lodeRunnerLevelSequenceAverages:true","lodeRunnerLevelSequenceIndividual:false", "lodeRunnerAllowsSimpleAStarPath:true", "lodeRunnerAllowsConnectivity:true", "base:loderunnerlevels", "log:LodeRunnerLevels-LevelSequence", "saveTo:LevelSequence", "LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "watch:false", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
			//to test individual
			//MMNEAT.main(new String[] {"runNumber:1", "randomSeed:2", "lodeRunnerLevelSequenceAverages:false","lodeRunnerLevelSequenceIndividual:true", "lodeRunnerAllowsSimpleAStarPath:true", "lodeRunnerAllowsConnectivity:true", "base:loderunnerlevels", "log:LodeRunnerLevels-LevelSequence", "saveTo:LevelSequence", "LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "watch:false", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});
			//to test the increasing difficulty, all three 
			MMNEAT.main(new String[] {"runNumber:3", "randomSeed:2", "lodeRunnerAllowsLinearIncreasingSolutionLength:true","lodeRunnerAllowsLinearIncreasingEnemyCount:true", "lodeRunnerAllowsLinearIncreasingTreasureCount:true","lodeRunnerLevelSequenceAverages:false","lodeRunnerLevelSequenceIndividual:false", "lodeRunnerAllowsSimpleAStarPath:false", "lodeRunnerAllowsConnectivity:false", "base:loderunnerlevels", "log:LodeRunnerLevels-LevelSequence", "saveTo:LevelSequence", "LodeRunnerGANModel:LodeRunnerAllGround100LevelsEpoch200000_10_7.pth", "watch:false", "GANInputSize:10", "trials:1", "mu:100", "maxGens:100000", "io:true", "netio:true", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "mating:true", "fs:false", "task:edu.southwestern.tasks.loderunner.LodeRunnerGANLevelSequenceTask", "cleanFrequency:-1", "saveAllChampions:true", "cleanOldNetworks:false", "logTWEANNData:false", "logMutationAndLineage:false", "steadyStateIndividualsPerGeneration:100", "aStarSearchBudget:100000"});

		} catch (FileNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public LodeRunnerGANLevelSequenceTask() {
		super();
	}


	@Override
	/**
	 * Gets the sequence of levels to be evolved 
	 * @return An array of levels 
	 */
	public ArrayList<List<List<Integer>>> getLevelSequence(Genotype<List<Double>> individual, int numOfLevels) {
		ArrayList<List<List<Integer>>> levelSequence = new ArrayList<>(numOfLevels);
		List<Double> phenotype = individual.getPhenotype();
		for(int j = 0; j < phenotype.size(); j+=Parameters.parameters.integerParameter("GANInputSize")) {
			List<Double> oneLevelPhenotype = phenotype.subList(j, j+Parameters.parameters.integerParameter("GANInputSize"));
			List<List<Integer>> level = getLodeRunnerLevelListRepresentationFromStaticGenotype(oneLevelPhenotype);
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
	public double differentRandomSeedForEveryLevel(int levelInSequence, List<Double> phenotype) {
		// The first latent variable of each level is treated as a random seed for determining spawn point
		return phenotype.get(levelInSequence*Parameters.parameters.integerParameter("GANInputSize"));
	}

	/**
	 * Gets a level from the genotype
	 * @return A level 
	 */
	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<List<Double>> individual) {
		return getLodeRunnerLevelListRepresentationFromStaticGenotype(individual.getPhenotype());
	}

	/**
	 * Calls the method written in LodeRunnerGANLevelTask to return a level from a phenotype
	 * @param phenotype
	 * @return
	 */
	public static List<List<Integer>> getLodeRunnerLevelListRepresentationFromStaticGenotype(List<Double> phenotype) {
		return LodeRunnerGANLevelTask.getLodeRunnerLevelListRepresentationFromGenotypeStatic(phenotype);
	}
}
