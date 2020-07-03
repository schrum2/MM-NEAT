package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.StatisticsUtilities;


public abstract class LodeRunnerLevelSequenceTask<T> extends LodeRunnerLevelTask<T> {
	private static final int numOtherScores = 8;
	private static int numFitnessFunctions = 0;

	public LodeRunnerLevelSequenceTask() {
		super(false); // Do not register the fitness functions in the LodeRunnerLevelTask

		//If we are averaging scores then we add all of the scores from the LodeRunnerLevelTask because it will take the averages from each level in the sequence
		if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages")) {
			//one fitness function of each type to average across all the levels in the sequence
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) { 
				MMNEAT.registerFitnessFunction("averageSimpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("averageNumOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}
		}
		//if we are taking individual scores it takes all the scores as for the first level, then adds all the scores for the second level, and so on for all levels
		//it maintains order by looping for the amount of levels in the sequence
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			for(int i = 0; i < Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"); i++) { //one fitness function of each type for each level in the sequence 
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
					MMNEAT.registerFitnessFunction("Level"+i+"simpleAStarDistance");
					numFitnessFunctions++;
				}
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
					MMNEAT.registerFitnessFunction("Level"+i+"numOfPositionsVisited"); //connectivity
					numFitnessFunctions++;
				}
			}
		}
		else { //use the increasing difficulty fitness functions
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingSolutionLength")) { 
				MMNEAT.registerFitnessFunction("linearIncreasingSolutionLengthFit");
				MMNEAT.registerFitnessFunction("linearIncreasingSolutionLengthRange");
				numFitnessFunctions+=2;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingEnemyCount")) {
				MMNEAT.registerFitnessFunction("linearIncreasingEnemyCountFit");
				MMNEAT.registerFitnessFunction("linearIncreasingEnemyCountRange");
				numFitnessFunctions+=2;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTreasureCount")) {
				MMNEAT.registerFitnessFunction("linearIncreasingTreasureCountFit");
				MMNEAT.registerFitnessFunction("linearIncreasingTreasureCountRange");
				numFitnessFunctions+=2;
			}
		}

		//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
		for(int i = 1; i <= Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"); i++) {
			MMNEAT.registerFitnessFunction("Level" + i + "simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("Level" + i + "numOfPositionsVisited",false); //connectivity
			MMNEAT.registerFitnessFunction("Level" + i + "percentLadders", false);
			MMNEAT.registerFitnessFunction("Level" + i + "percentGround", false);
			MMNEAT.registerFitnessFunction("Level" + i + "percentRope", false);
			MMNEAT.registerFitnessFunction("Level" + i + "percentConnected", false);
			MMNEAT.registerFitnessFunction("Level" + i + "numTreasures", false);
			MMNEAT.registerFitnessFunction("Level" + i + "numEnemies", false);
		}
	}

	/**
	 * @return The number of fitness functions 
	 */
	@Override
	public int numObjectives() {
		return numFitnessFunctions; 
	}

	/**
	 * 8 if average, 8*number of levels if individual 
	 * @return The number of other scores 
	 */
	@Override
	public int numOtherScores() {
		//8* number of levels because we always take every otherScore from each level
		return numOtherScores*Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence");
	}

	/**
	 * Overrides the oneEval method of LodeRunnerLevelTask to 
	 * evaluate all of the levels of the sequence instead of just a single level
	 * @return The scores 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		ArrayList<List<List<Integer>>> levelSequence = getLevelSequence(individual, Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"));//right now I set it to have 3 levels in the sequence
		long genotypeId = individual.getId();
		Pair<double[], double[]>[] scoreSequence = new Pair[levelSequence.size()];
		for(int i = 0; i < levelSequence.size(); i++) {
			//takes in the level it is on, i, and the length of the levelSequence
			double psuedoRandomSeed = differentRandomSeedForEveryLevel(i, levelSequence.size()); //different random seed for every level in the sequence
			scoreSequence[i] = evaluateOneLevel(levelSequence.get(i), psuedoRandomSeed, genotypeId);
		}
		Pair<double[], double[]> finalScores; //declares variable to hold the final scores to be calculated
		//calculate the otherScores
		double[] otherScores = new double[scoreSequence[0].t2.length*Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")]; //new double array that is the size of the other sores array
		for(int i = 0; i < scoreSequence.length; i++) {
			for(int k = 0; k < scoreSequence[i].t2.length; k++) {
				otherScores[i*scoreSequence[i].t2.length+k] = scoreSequence[i].t2[k]; //calculates the all of the other scores for every level 
			}
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages")) {
			//average all the scores together so that there are as many scores as levels
			double[] averageFitnesses = new double[scoreSequence[0].t1.length]; //new double array that is the size of the fitness functions array
			//calculates the total scores from all levels 
			for(int i = 0; i < scoreSequence.length; i++) {
				for(int j = 0; j < averageFitnesses.length; j++) {
					averageFitnesses[j] += scoreSequence[i].t1[j]; //sums all of the scores from all the levels to be averaged
				}
			}
			//averages the values in the fitness array by dividing the sum of those values by the amount of levels
			for(int i = 0; i < averageFitnesses.length; i++) {
				averageFitnesses[i] = averageFitnesses[i]/Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence");
			}
			finalScores = new Pair<double[], double[]>(averageFitnesses, otherScores);
		}
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			//individual scores, this means it is the amount of scores times the amount of levels
			//new double array that is the length to fit all the fitnesses from every level in the sequence
			double[] allFitnesses = new double[scoreSequence[0].t1.length*Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")]; 
			//adds all the scores from the level sequence to the new arrays 
			for(int i = 0; i < scoreSequence.length; i++) {
				for(int j = 0; j < allFitnesses.length; j++) {
					allFitnesses[j] = scoreSequence[i].t1[j];
				}
			}
			finalScores = new Pair<double[], double[]>(allFitnesses, otherScores);
		}
		else {
			ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array
			double simpleAStarDistance;
			double meanSquaredErrorAStar;
			double numEnemies;
			double meanSquaredErrorEnemies;
			double treasureCount;
			double meanSquaredErrorTreasures;
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingSolutionLength")) {
				int offset = 0; //simpleAStarDistance is index 0 of the each level in otherScores
				double[] simpleAStarDistancePerLevel =  calculateIncreasingFitnesses(otherScores, offset); //collects the values for fitness from the other scores
				double[] meanSquaredErrorsAStar = calculateMeanSquaredErrors(simpleAStarDistancePerLevel, scoreSequence);
				simpleAStarDistance = StatisticsUtilities.average(simpleAStarDistancePerLevel);
				meanSquaredErrorAStar = StatisticsUtilities.average(meanSquaredErrorsAStar);
				fitnesses.add(simpleAStarDistance);
				fitnesses.add(meanSquaredErrorAStar);
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingEnemyCount")) {
				int offset = 7; //numOfEnemies is index 7 of the each level in otherScores
				double[] numEnemiesPerLevel = calculateIncreasingFitnesses(otherScores, offset);//collects the values for fitness from the other scores
				double[] meanSquaredErrorsEnemies = calculateMeanSquaredErrors(numEnemiesPerLevel, scoreSequence);
				numEnemies =  StatisticsUtilities.average(numEnemiesPerLevel);
				meanSquaredErrorEnemies =  StatisticsUtilities.average(meanSquaredErrorsEnemies);
				fitnesses.add(numEnemies);
				fitnesses.add(meanSquaredErrorEnemies);
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTreasureCount")) {
				int offset = 6; //treasureCount is index 6 of the each level in otherScores
				double[] treasureCountPerLevel = calculateIncreasingFitnesses(otherScores, offset);//collects the values for fitness from the other scores
				double[] meanSquaredErrorsTreasures = calculateMeanSquaredErrors(treasureCountPerLevel, scoreSequence);
				treasureCount = StatisticsUtilities.average(treasureCountPerLevel);
				meanSquaredErrorTreasures = StatisticsUtilities.average(meanSquaredErrorsTreasures);
				fitnesses.add(treasureCount);
				fitnesses.add(meanSquaredErrorTreasures);
			}
			finalScores = new Pair<double[], double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
		}
		return finalScores;

	}

	/** 
	 * Calculates the mean Squared differences between the fitness and the expected
	 * @param fitnesses
	 * @param scoreSequence
	 * @return
	 */
	public double[] calculateMeanSquaredErrors(double[] fitnesses,
			Pair<double[], double[]>[] scoreSequence) {
		double[] meanSquaredErrors = new double[fitnesses.length];
		//fills the expected array and the fitness array
		double[] squaredErrors = new double[fitnesses.length]; 
		double[] expected = new double[fitnesses.length];
		for(int j = 0; j < fitnesses.length; j++) {
			double slope = scoreSequence[j].t1[scoreSequence[j].t1.length-1]-scoreSequence[j].t1[0]/Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")-1; //calculates slope found by dividing the difference of the y values by the difference of the x values
			for(int i = 1; i < expected.length; i++) { //takes the middle values, excluding the first and last 
				expected[i] = slope*i + scoreSequence[j].t1[i]; //mx + b, where the and b is the min y value
			}
			squaredErrors = StatisticsUtilities.calculateSquaredErrors(scoreSequence[j].t1, expected);
			meanSquaredErrors[j] = StatisticsUtilities.average(squaredErrors);
		}
		return meanSquaredErrors;
	}

	/**
	 * Collects the correct values for fitness from the otherScores array
	 * @param otherScores
	 * @param offset
	 * @return
	 */
	public double[] calculateIncreasingFitnesses(double[] otherScores, int offset) {
		double[] fitnesses = new double[otherScores.length/Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")];
		for(int j = 0; j < fitnesses.length; j++) {
			for(int i = offset; i < otherScores.length; i+=8) {
				fitnesses[j] = otherScores[i];
			}
		}
		return fitnesses;
	}

	/**
	 * Gets a level from the genotype
	 * @return A level 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual) {
		return getLodeRunnerLevelListRepresentationFromStaticGenotype((List<Double>) individual.getPhenotype());
	}

	/**
	 * Calls the method written in LodeRunnerGANLevelTask to return a level from a phenotype
	 * @param phenotype
	 * @return
	 */
	public static List<List<Integer>> getLodeRunnerLevelListRepresentationFromStaticGenotype(List<Double> phenotype) {
		return LodeRunnerGANLevelTask.getLodeRunnerLevelListRepresentationFromGenotypeStatic(phenotype);
	}


	/**
	 * Gets a Random seed for the choosing of a spawn point for generated levels 
	 */
	@Override
	public double getRandomSeedForSpawnPoint(Genotype<T> individual) {
		return getRandomSeedForSpawnPointStatic(individual);
	}

	/**
	 * Called from non-static to return a random seed double 
	 * @param individual
	 * @return Random seed 
	 */
	@SuppressWarnings("unchecked")
	private double getRandomSeedForSpawnPointStatic(Genotype<T> individual) {
		List<Double> latentVector = (List<Double>) individual.getPhenotype(); //creates a double array for the spawn to be placed in GAN levels 
		double[] doubleArray = ArrayUtil.doubleArrayFromList(latentVector);
		double firstLatentVariable = doubleArray[0];
		return firstLatentVariable;
	}

	/**
	 * Gets a sequence of levels 
	 * @param individual Genoty[e
	 * @param numOfLevels Number of levels in the sequence
	 * @return An array holding the number of levels specified
	 */
	public abstract ArrayList<List<List<Integer>>> getLevelSequence(Genotype<T> individual, int numOfLevels);

	/**
	 * Gets a different random seed for all of the levels in the sequence
	 * @param levelInSequence The level that needs a random seed
	 * @param lengthOfSequence Amount of levels in the sequence
	 * @return Random seed for the level specified 
	 */
	public abstract double differentRandomSeedForEveryLevel(int levelInSequence, int lengthOfSequence);


}
