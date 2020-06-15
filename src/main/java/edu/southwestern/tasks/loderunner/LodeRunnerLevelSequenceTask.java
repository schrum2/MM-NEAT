package edu.southwestern.tasks.loderunner;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;


public abstract class LodeRunnerLevelSequenceTask<T> extends LodeRunnerLevelTask<T> {
	private static final int numOtherScores = 8;
	private static int numFitnessFunctions = 0;

	public LodeRunnerLevelSequenceTask() {
		super(false); // Do not register the fitness functions in the LodeRunnerLevelTask
		
		// Register fitness functions specific to the level sequence task here instead.
		// TODO! (consider which command line parameters are active: average vs each individual)
		
		//If we are averaging scores then we add all of the scores from the LodeRunnerLevelTask because it will take the averages from each level in the sequence
		if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages")) {
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
				MMNEAT.registerFitnessFunction("simpleAStarDistance");
				numFitnessFunctions++;
			}
			if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
				MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
				numFitnessFunctions++;
			}

			//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
			MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
			MMNEAT.registerFitnessFunction("numOfPositionsVisited",false); //connectivity
			MMNEAT.registerFitnessFunction("percentLadders", false);
			MMNEAT.registerFitnessFunction("percentGround", false);
			MMNEAT.registerFitnessFunction("percentRope", false);
			MMNEAT.registerFitnessFunction("percentConnected", false);
			MMNEAT.registerFitnessFunction("numTreasures", false);
			MMNEAT.registerFitnessFunction("numEnemies", false);
		}
		//if we are taking individual scores it takes all the scores as for the first level, then adds all the scores for the second level, and so on for all levels
		//it maintains order by looping for the amount of levels in the sequence
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			for(int i = 0; i < Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence"); i++) {
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsSimpleAStarPath")) {
					MMNEAT.registerFitnessFunction("simpleAStarDistance");
					numFitnessFunctions++;
				}
				if(Parameters.parameters.booleanParameter("lodeRunnerAllowsConnectivity")) {
					MMNEAT.registerFitnessFunction("numOfPositionsVisited"); //connectivity
					numFitnessFunctions++;
				}

				//registers the other things to be tracked that are not fitness functions, to be put in the otherScores array 
				MMNEAT.registerFitnessFunction("simpleAStarDistance",false);
				MMNEAT.registerFitnessFunction("numOfPositionsVisited",false); //connectivity
				MMNEAT.registerFitnessFunction("percentLadders", false);
				MMNEAT.registerFitnessFunction("percentGround", false);
				MMNEAT.registerFitnessFunction("percentRope", false);
				MMNEAT.registerFitnessFunction("percentConnected", false);
				MMNEAT.registerFitnessFunction("numTreasures", false);
				MMNEAT.registerFitnessFunction("numEnemies", false);
			}
		}else throw new UnsupportedOperationException("Don't test LodeRunnerLevelSequenceTask again without first registering fitness functions");
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
		//returns 8 if we are taking the averages of the scores and 8* number of levels if we are taking all scores from each individual
		return Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual") ? 
				numOtherScores*Parameters.parameters.integerParameter("lodeRunnerNumOfLevelsInSequence") : numOtherScores;
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
			double psuedoRandomSeed = differentRandomSeedForEveryLevel(i, levelSequence.size()); // TODO: Different seed for each level in the sequnce ... needs abstract method
			scoreSequence[i] = evaluateOneLevel(levelSequence.get(i), psuedoRandomSeed, genotypeId);
		}
		if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceAverages")) {
			//average all the scores together so that there are as many scores as levels
			double[] averageFitnesses = new double[scoreSequence[0].t1.length]; //new double array that is the size of the fitness functions array
			double[] averageOtherScores = new double[scoreSequence[0].t2.length]; //new double array that is the size of the other sores array
			//calculates the total scores from all levels 
			for(int i = 0; i < scoreSequence.length; i++) {
				for(int j = 0; j < averageFitnesses.length; j++) {
					for(int k = 0; k < averageOtherScores.length; k++) {
						averageFitnesses[j] += scoreSequence[i].t1[j];
						averageOtherScores[k] += scoreSequence[i].t2[k];
					}
				}
			}
			//averages the values in the fitness array
			for(int i = 0; i < averageFitnesses.length; i++) {
				averageFitnesses[i] /= scoreSequence.length;
			}
			//finds the averages of the other scores array
			for(int i = 0; i < averageOtherScores.length; i++) {
				averageOtherScores[i] /= scoreSequence.length;
			}
			return new Pair<double[], double[]>(averageFitnesses, averageOtherScores);
		}
		else if(Parameters.parameters.booleanParameter("lodeRunnerLevelSequenceIndividual")) {
			//individual scores, this means it is the amount of scores times the amount of levels
			//new double array that is the length to fit all the fitnesses from every level in the sequence
			double[] allFitnesses = new double[scoreSequence[0].t1.length*scoreSequence[0].t1.length]; 
			//new double array that is the length to fit all the fitnesses from every level in the sequence
			double[] allOtherScores = new double[scoreSequence[0].t2.length*scoreSequence[0].t2.length];
			//adds all the scores from the level sequence to the new arrays 
			for(int i = 0; i < scoreSequence.length; i++) {
				for(int j = 0; j < allFitnesses.length; j++) {
					for(int k = 0; k < allOtherScores.length; k++) {
						allFitnesses[j] = scoreSequence[i].t1[j];
						allOtherScores[k] = scoreSequence[i].t2[k];
					}
				}
			}
			return new Pair<double[], double[]>(allFitnesses, allOtherScores);
		}
		else 
			return null;	
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
	private static List<List<Integer>> getLodeRunnerLevelListRepresentationFromStaticGenotype(List<Double> phenotype) {
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
