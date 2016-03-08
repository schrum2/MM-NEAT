package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;

/**
 * Stores a genotype, which can be replaced as needed.
 * @author Jacob Schrum
 */
public class Organism<T> {

	// Store a genotype that produces a phenotype T
    protected Genotype<T> genotype;

    // Constructor
    public Organism(Genotype<T> genotype) {
        this.genotype = genotype;
    }

    // Get
    public Genotype<T> getGenotype() {
        return genotype;
    }

    /**
     * Replace the genotype of the organism so the same organism
     * can be evaluated in the domain, but with a new genotype.
     * @param newGenotype The new genotype
     */
    public void replaceGenotype(Genotype<T> newGenotype) {
        this.genotype = newGenotype;
    }
}
