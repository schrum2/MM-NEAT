package edu.southwestern.evolution.fitness;

import edu.southwestern.evolution.Organism;

/**
 * Basic interface for fitness functions
 * 
 * @author Jacob Schrum
 * @param <T> Phenotype of organisms
 */
public interface FitnessFunction<T> {

	/**
	 * Given an organism, return its fitness.
	 * 
	 * This mostly applies to meta-heuristics. Most fitness values from
	 * evolution depend on the Score instance returned from evaluation.
	 * 
	 * @param individual Organism evaluated
	 * @return fitness score as a double.
	 */
	public double fitness(Organism<T> individual);
}
