package edu.southwestern.tasks;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import java.util.ArrayList;

/**
 * A task for evolving a single population. The evaluateAll
 * method takes the entire population. This flexibility
 * allows population members to interact with each other
 * possibly.
 * 
 * @author Jacob Schrum
 * @param <T> phenotype possessed by members of population
 */
public interface SinglePopulationTask<T> extends Task {
	/**
	 * a method that obtains a list of score evaluations of all of the genotypes
	 * of the population
	 * 
	 * @param population
	 * @return list of scores
	 */
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population);
}
