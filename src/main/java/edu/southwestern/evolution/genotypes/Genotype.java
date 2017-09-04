package edu.southwestern.evolution.genotypes;

import java.util.List;

/**
 * Basic interface for all genotypes.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype produced by the genotype
 */
public interface Genotype<T> {

	/**
	 * Indicate the ID of a parent of this genotype.
	 * Can be called multiple times.
	 * @param id Genotype ID of a parent of this genotype
	 */
	public void addParent(long id);
	
	/**
	 * Get ids of all parents of this genotype
	 * @return List of all parent genotype ids
	 */
	public List<Long> getParentIDs();
	
	/**
	 * Make and return a copy of the genotype
	 * 
	 * @return copy of genotype
	 */
	public Genotype<T> copy();

	/**
	 * Mutate the genotype.
	 * 
	 * This operation can probabilistically call a variety of other specific
	 * mutation operators. Calling this method does not guarantee that the
	 * genotype will be modified.
	 */
	public void mutate();

	/**
	 * Cross this genotype with another genotype g to create up to two new
	 * offspring. One of the offspring is returned. Additionally, this genotype
	 * itself may be modified, thus representing another offspring, but this
	 * would only be appropriate if crossover is performed on a copy of the
	 * original genotype.
	 * 
	 * @param g
	 *            genotype to crossover with
	 * @return one offspring
	 */
	public Genotype<T> crossover(Genotype<T> g);

	/**
	 * Decode the genotype to produce the phenotype
	 * 
	 * @return A phenotype of type T
	 */
	public T getPhenotype();

	/**
	 * Any instance of a genotype has the capacity to create an entirely new
	 * instance. This method is typically used at the beginning of evolution to
	 * initialize the population.
	 * 
	 * @return New genotype for starting population
	 */
	public Genotype<T> newInstance();

	/**
	 * Every genotype should have a unique ID assigned by EvolutionaryHistory
	 * 
	 * @return unique ID number
	 */
	public long getId();
}
