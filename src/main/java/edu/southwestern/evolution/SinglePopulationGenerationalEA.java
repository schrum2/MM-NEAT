package edu.southwestern.evolution;

import edu.southwestern.evolution.genotypes.Genotype;
import java.util.ArrayList;

/**
 * Interface for basic Evolutionary Algorithm that evolves a single population
 * (no coevolution) one generation at a time.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype of evolved organisms
 */
public interface SinglePopulationGenerationalEA<T> extends GenerationalEA {

	/**
	 * Get initial population given an example of one individual. The new
	 * population will consist of random individuals generated according to
	 * whatever constraints restrict the structure of the example Genotype.
	 *
	 * @param example
	 *            example genotype
	 * @return randomized population
	 */
	public ArrayList<Genotype<T>> initialPopulation(Genotype<T> example);

	/**
	 * Evolve one generation forward by evaluating population, keeping the best,
	 * and doing crossover and mutation to get a new population, which is
	 * returned.
	 *
	 * @param population
	 *            population to evaluate and evolve
	 * @return new population for next generation.
	 */
	public ArrayList<Genotype<T>> getNextGeneration(ArrayList<Genotype<T>> population);

	/**
	 * Do ending steps to get final bits of data and/or clean up simulation
	 * before ending evolution.
	 *
	 * @param population
	 *            final population produced by evolution
	 */
	public void close(ArrayList<Genotype<T>> population);
}
