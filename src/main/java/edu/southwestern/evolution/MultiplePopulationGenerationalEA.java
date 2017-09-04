package edu.southwestern.evolution;

import edu.southwestern.evolution.genotypes.Genotype;

import java.util.ArrayList;

/**
 * Annoying Programming Languages problem: Want to allow an arbitrary number of
 * subpopulations, and allow each one to hold a different type of genotype.
 * Cannot support both options. Either need to designate a specific number and
 * provide a type variable for each one, or use an arbitrary length structure
 * like ArrayList with the specific type of Genotype undefined, so that each one
 * can be different.
 *
 * @author Jacob Schrum
 */
public interface MultiplePopulationGenerationalEA extends GenerationalEA {

	/**
	 * Initializes a sub-population of genotypes corresponding to each example genotype 
	 * in the provided array list.
	 * @param examples 
	 * 		example genotypes that are used to make the starting population of organisms
	 * @return randomized population
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Genotype>> initialPopulations(ArrayList<Genotype> examples);

	/**
	 * Given a list of all sub-populations, derive the next generation consisting of a new sub-population 
	 * derived from each input population.
	 * @param populations
	 * 			populations to evaluate and evolve
	 * @return new population of organisms from the next generation
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList<ArrayList<Genotype>> getNextGeneration(ArrayList<ArrayList<Genotype>> populations);

	/**
	 * Carry out any operations at the end of the experiment (such as logging information about the 
	 * final population)
	 * @param populations
	 * 			final population produced by evolution
	 */
	@SuppressWarnings("rawtypes")
	public void close(ArrayList<ArrayList<Genotype>> populations);
}
