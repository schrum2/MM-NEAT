package edu.utexas.cs.nn.tasks;

import java.util.ArrayList;
import java.util.Collections;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.scores.MultiObjectiveScore;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.stats.Statistic;

public abstract class SinglePopulationCoevolutionTask<T> implements SinglePopulationTask<T>{

	private Statistic stat;

	public SinglePopulationCoevolutionTask() {
		try {
			stat = (Statistic) ClassCreation.createObject("noisyTaskStat");
		} catch (NoSuchMethodException ex) {
			ex.printStackTrace();
			System.exit(1);
		}		
	}
	
	@Override
	public double getTimeStamp() {
		// Many Domains don't use TimeStamp
		return 0;
	}

	@Override
	public void finalCleanup() {
		// Default to empty
	}

	public abstract int groupSize();
	
	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		// Used to randomly group agents in the population
		ArrayList<Integer> groupOrder = new ArrayList<Integer>(population.size());
		// Track scores
		ArrayList<ArrayList<Pair<double[], double[]>>> allScores = new ArrayList<ArrayList<Pair<double[], double[]>>>(population.size());
		for(int i = 0; i < population.size(); i++) {
			groupOrder.add(i); // Index in population
			allScores.add(new ArrayList<Pair<double[], double[]>>(CommonConstants.trials)); // anticipate one batch of scores per trial
		}
		
		int groupSize = groupSize(); // Replace: make task dependent
		
		assert population.size()%groupSize == 0;
		
		for(int i = 0; i < CommonConstants.trials; i++){
			Collections.shuffle(groupOrder); // Randomize who individuals are grouped with
			for(int j = 0; j < population.size(); j+= groupSize){ // for each group
				// Assign individuals to the group to be evaluated
				ArrayList<Genotype<T>> group = new ArrayList<Genotype<T>>(groupSize);
				for(int k = 0; k < groupSize; k++) {
					group.add(population.get(groupOrder.get(j+k)));
				}
				// Get scores
				ArrayList<Pair<double[], double[]>> result = evaluateGroup(group);
				// Save scores in the right place
				for(int k = 0; k < groupSize; k++) {
					allScores.get(groupOrder.get(j+k)).add(result.get(k));
				}
			}
		}
		
		// Collect scores
		ArrayList<Score<T>> scores = new ArrayList<Score<T>>(population.size());
		for(int k = 0; k < population.size(); k++) {
			double[] fitness = new double[this.numObjectives()];
			// Aggregate each fitness score across all trials
			for (int i = 0; i < fitness.length; i++) {
				// Add aggregation overrides?
				fitness[i] = stat.stat(scoresFromIndividual(allScores.get(k), true, i));
			}
			double[] other = new double[this.numOtherScores()];
			// Aggregate each other score across all trials
			for (int i = 0; i < other.length; i++) {
				// Add aggregation overrides?
				other[i] = stat.stat(scoresFromIndividual(allScores.get(k), false, i));
			}
			// Need way to support behavioral diversity for coevolution
			Score<T> s = new MultiObjectiveScore<T>(population.get(k), fitness, null, other);
			scores.add(s);
		}

		return scores;
	}

	/**
	 * Helper method; Extracts the information from an ArrayList of Pairs of Double Arrays
	 * 
	 * Returns a double[] containing the data from a specific index of one specified double[] from all Pairs in the given ArrayList
	 * 
	 * @param arrayList The ArrayList from which to extract information
	 * @param fit Is the information needed from the Fitness Pair?
	 * @param column The specified Column to extract information from
	 * @return double[] containing the extracted column of information
	 */
	private double[] scoresFromIndividual(ArrayList<Pair<double[], double[]>> arrayList, boolean fit, int column) {
		double[] info = new double[arrayList.size()];
		int index = 0;
		
		for(Pair<double[], double[]> pair : arrayList){
			double[] doubleArray = fit ? pair.t1 : pair.t2;
			info[index++] = doubleArray[column];
		}
		return info;
	}

	public int numOtherScores() {
		return 0;
	}
	
	/**
	 * Evaluates the Genotypes in a given ArrayList<Genotype<T>> and saves the fitness for each individual
	 * 
	 * @param group
	 * @return ArrayList<Pair<double[], double[]>> representing the fitness of the individuals in the group
	 */
	public abstract ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group);
}
