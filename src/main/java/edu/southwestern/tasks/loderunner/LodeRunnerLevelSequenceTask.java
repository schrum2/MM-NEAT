package edu.southwestern.tasks.loderunner;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;


public abstract class LodeRunnerLevelSequenceTask<T> extends LodeRunnerLevelTask<T> {

	public LodeRunnerLevelSequenceTask() {
		super();
	}

	/**
	 * Overrides the oneEval method of LodeRunnerLevelTask to 
	 * evaluate all of the levels of the sequence instead of just a single level
	 * @return The scores 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num){
		List<List<Integer>>[] levelSequence = getLevelSequence(individual, 5);//right now I set it to have 5 levels in the sequence
		long genotypeId = individual.getId();
		Pair<double[], double[]>[] scoreSequence = new Pair[levelSequence.length];
		for(int i = 0; i < levelSequence.length; i++) {
			//takes in the level it is on, i, and the length of the levelSequence
			double psuedoRandomSeed = differentRandomSeedForEveryLevel(i, levelSequence.length); // TODO: Different seed for each level in the sequnce ... needs abstract method
			scoreSequence[i] = evaluateOneLevel(levelSequence[i], psuedoRandomSeed, genotypeId);
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
	public abstract List<List<Integer>>[] getLevelSequence(Genotype<T> individual, int numOfLevels);

	/**
	 * Gets a different random seed for all of the levels in the sequence
	 * @param levelInSequence The level that needs a random seed
	 * @param lengthOfSequence Amount of levels in the sequence
	 * @return Random seed for the level specified 
	 */
	public abstract double differentRandomSeedForEveryLevel(int levelInSequence, int lengthOfSequence);


}
