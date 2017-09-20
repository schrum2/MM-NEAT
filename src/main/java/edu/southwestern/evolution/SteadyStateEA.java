package edu.southwestern.evolution;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;

/**
 * Basic interface for an evolutionary algorithm that
 * gradually adds one new individual at a time.
 * @author Dr. Schrum
 */
public interface SteadyStateEA<T> extends EA {
	/**
	 * Initialize the EA, which will generally include creating some
	 * starting population.
	 * @param example Example genotype to base population members on
	 */
	public void initialize(Genotype<T> example);
	/**
	 * Create a new individual to add (potentially) to the population
	 */
	public void newIndividual();
	/**
	 * Number of iterations of the steady state EA
	 * @return Number of iterations
	 */
	public int currentIteration();
	/**
	 * Anything that must happen at the end of evolution
	 */
	public void finalCleanup();
	/**
	 * Return population as a list of genotypes
	 * @return Array List of Genotypes 
	 */
	public ArrayList<Genotype<T>> getPopulation();
	/**
	 * Track whether population just changed (not always true in steady state EA)
	 * @return Whether population just changed
	 */
	public boolean populationChanged();
}
