package edu.southwestern.evolution;

import edu.southwestern.evolution.genotypes.Genotype;

/**
 * Stores a genotype, which can be replaced as needed.
 *
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype being evolved
 */
public class Organism<T> {

	// Store a genotype that produces a phenotype T
	protected Genotype<T> genotype;

	/**
	 * Put genotype in Organism
	 * 
	 * @param genotype
	 *            generates organism
	 */
	public Organism(Genotype<T> genotype) {
		this.genotype = genotype;
	}

	/**
	 * Genotype that generated organism
	 * 
	 * @return the genotype
	 */
	public Genotype<T> getGenotype() {
		return genotype;
	}

	/**
	 * Replace the genotype of the organism so the same organism can be
	 * evaluated in the domain, but with a new genotype.
	 *
	 * @param newGenotype
	 *            The new genotype
	 */
	public void replaceGenotype(Genotype<T> newGenotype) {
		this.genotype = newGenotype;
	}
}
