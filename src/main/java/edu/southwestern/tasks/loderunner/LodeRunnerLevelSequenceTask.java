package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.stats.StatisticsUtilities;


public abstract class LodeRunnerLevelSequenceTask<T> extends LodeRunnerLevelTask<T> {
	private int numFitnessFunctions = 0;
	private static final int ASTAR_OFFSET = 0;
	private static final int NUM_TREASURES_OFFSET = 6;
	private static final int NUM_ENEMIES_OFFSET = 7;
	private static final int TSP_OFFSET = 8;

	public LodeRunnerLevelSequenceTask() {
		super(false); // Do not register the fitness functions in the LodeRunnerLevelTask

		assert !(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages") && Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) :
				"Do not evolve level sequences with both average across levels and individual fitnesses of levels";
		
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
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsTSPSolutionPath")) {
				MMNEAT.registerFitnessFunction("averageTSPSolutionLength"); //connectivity
				numFitnessFunctions++;
			}
		}
		//if we are taking individual scores it takes all the scores as for the first level, then adds all the scores for the second level, and so on for all levels
		//it maintains order by looping for the amount of levels in the sequence
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			for(int i = 1; i <= Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"); i++) { //one fitness function of each type for each level in the sequence 
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
					MMNEAT.registerFitnessFunction("Level"+i+"simpleAStarDistance");
					numFitnessFunctions++;
				}
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
					MMNEAT.registerFitnessFunction("Level"+i+"numOfPositionsVisited"); //connectivity
					numFitnessFunctions++;
				}
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsTSPSolutionPath")) {
					MMNEAT.registerFitnessFunction("Level"+i+"TSPSolutionLength"); 
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
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTSPLength")) { 
				MMNEAT.registerFitnessFunction("linearIncreasingTSPSolutionLengthFit");
				MMNEAT.registerFitnessFunction("linearIncreasingTSPSolutionLengthRange");
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
			MMNEAT.registerFitnessFunction("Level" + i + "TSPDistance", false);
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
		return LodeRunnerLevelTask.NUM_OTHER_SCORES*Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence");
	}

	/**
	 * Overrides the oneEval method of LodeRunnerLevelTask to 
	 * evaluate all of the levels of the sequence instead of just a single level
	 * @return The scores 
	 */
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		ArrayList<List<List<Integer>>> levelSequence = getLevelSequence(individual, Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"));//right now I set it to have 3 levels in the sequence
		long genotypeId = individual.getId();
		@SuppressWarnings("unchecked")
		Pair<double[], double[]>[] scoreSequence = new Pair[levelSequence.size()];
		for(int i = 0; i < levelSequence.size(); i++) {
			//takes in the level it is on, i, and the length of the levelSequence
			double psuedoRandomSeed = differentRandomSeedForEveryLevel(i, individual.getPhenotype()); //different random seed for every level in the sequence
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
				for(int j = 0; j < scoreSequence[i].t1.length; j++) {
					allFitnesses[(i*scoreSequence[i].t1.length) +j] = scoreSequence[i].t1[j];
				}
			}
			finalScores = new Pair<double[], double[]>(allFitnesses, otherScores);
		}
		else {
			ArrayList<Double> fitnesses = new ArrayList<>(numFitnessFunctions); //initializes the fitness function array
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingSolutionLength")) {
				addLineFitAndRangeToFitness(otherScores, fitnesses, ASTAR_OFFSET); //adds the meanSquaredError and range of values as fitness functions
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTSPLength")) { 
				addLineFitAndRangeToFitness(otherScores, fitnesses, TSP_OFFSET); //adds the meanSquaredError and range of values as fitness functions
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingEnemyCount")) {
				addLineFitAndRangeToFitness(otherScores, fitnesses, NUM_ENEMIES_OFFSET);//adds the meanSquaredError and range of values as fitness functions
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsLinearIncreasingTreasureCount")) {
				addLineFitAndRangeToFitness(otherScores, fitnesses, NUM_TREASURES_OFFSET);//adds the meanSquaredError and range of values as fitness functions
			}
			finalScores = new Pair<double[], double[]>(ArrayUtil.doubleArrayFromList(fitnesses), otherScores);
		}
		return finalScores;
	}

	/**
	 * Adds the fitness functions for error of line fit and the range of the y values 
	 * @param otherScores Source of data for calculations
	 * @param fitnesses Where you add the final fitness scores too
	 * @param offset Where to find the fitness scores in other scores
	 */
	private void addLineFitAndRangeToFitness(double[] otherScores, ArrayList<Double> fitnesses, int offset) {
		double[] fitnessesFromOtherScores =  calculateIncreasingFitnesses(otherScores, offset); //collects the values for fitness from the other scores
		double meanSquaredErrorAStar = calculateMeanSquaredLineError(fitnessesFromOtherScores); //calculates the meanSquared error for the fitnesses 
		fitnesses.add(-meanSquaredErrorAStar); //mean squared error from fit of line. Make NEGATIVE because we want to minimize error (0 is best score)
		fitnesses.add(fitnessesFromOtherScores[fitnessesFromOtherScores.length-1] - fitnessesFromOtherScores[0]); //adds the range of values (max-min)
	}

	/** 
	 * Calculates the mean Squared differences between the fitness and the expected
	 * @param fitnesses
	 * @param scoreSequence
	 * @return
	 */
	public double calculateMeanSquaredLineError(double[] fitnesses) {
		//fills the expected array and the fitness array
		double[] expected = new double[fitnesses.length]; //will hold the expected values, line of best fit, which is built in the loop below 
		double min = StatisticsUtilities.minimum(fitnesses);
		double max = StatisticsUtilities.maximum(fitnesses);
		double slope = (max - min)/(fitnesses.length-1); //calculates slope found by dividing the difference of the y values by the difference of the x values
		for(int j = 0; j < fitnesses.length; j++) {
			expected[j] = slope*j + min; //mx + b, where the and b is the min y value, m is the slope from the from the first point to the last point, and x is the level
		}
		if(CommonConstants.watch) {
			System.out.println("Fitness : " + Arrays.toString(fitnesses));
			System.out.println("Expected: " + Arrays.toString(expected));
			System.out.println("Press enter");
			MiscUtil.waitForReadStringAndEnterKeyPress();
		}
		//will hold the squared errors between the expected values and the actual values
		double[] squaredErrors = StatisticsUtilities.calculateSquaredErrors(fitnesses, expected);// squaredErrors between the actual fitness and the expected line
		return StatisticsUtilities.average(squaredErrors); //returns the meanSquaredError
	}

	/**
	 * Collects the correct values for fitness from the otherScores array
	 * @param otherScores
	 * @param offset
	 * @return
	 */
	public double[] calculateIncreasingFitnesses(double[] otherScores, int offset) {
		double[] fitnesses = new double[Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence")];
		int index = 0;
		for(int i = offset; i < otherScores.length; i+=LodeRunnerLevelTask.NUM_OTHER_SCORES) {
			fitnesses[index++] = otherScores[i];//adds all the fitnesses of that type to the fitnesses array to be returned
		}
		return fitnesses; //returns an array that holds all of the specified fitness scores
	}

	// Moved this code up into the LevelSequenceGANTask, since it was assuming the phenotype was a List<Double>
//	/**
//	 * Gets a level from the genotype
//	 * @return A level 
//	 */
//	@Override
//	public List<List<Integer>> getLodeRunnerLevelListRepresentationFromGenotype(Genotype<T> individual) {
//		return getLodeRunnerLevelListRepresentationFromStaticGenotype((List<Double>) individual.getPhenotype());
//	}

//	/**
//	 * Calls the method written in LodeRunnerGANLevelTask to return a level from a phenotype
//	 * @param phenotype
//	 * @return
//	 */
//	public static List<List<Integer>> getLodeRunnerLevelListRepresentationFromStaticGenotype(List<Double> phenotype) {
//		return LodeRunnerGANLevelTask.getLodeRunnerLevelListRepresentationFromGenotypeStatic(phenotype);
//	}


	/**
	 * Gets a Random seed for the choosing of a spawn point for generated levels 
	 */
	@Override
	public double getRandomSeedForSpawnPoint(Genotype<T> individual) {
		throw new UnsupportedOperationException("Not actually used for level sequences. Rather, each level in the sequence needs its own random seed");
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
	 * @param individual the phenotype of the individual
	 * @return Random seed for the level specified 
	 */
	public abstract double differentRandomSeedForEveryLevel(int levelInSequence, T individual);


}
