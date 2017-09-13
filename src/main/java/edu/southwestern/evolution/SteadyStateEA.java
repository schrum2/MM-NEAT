package edu.southwestern.evolution;

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
	 * Whether the EA should stop.
	 * @return True if evolution is done
	 */
	public boolean shouldStop();
}
