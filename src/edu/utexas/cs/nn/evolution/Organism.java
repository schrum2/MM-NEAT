package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;

/**
 *
 * @author Jacob Schrum
 */
public class Organism<T> {

    protected Genotype<T> genotype;

    public Organism(Genotype<T> genotype) {
        this.genotype = genotype;
    }

    public Genotype<T> getGenotype() {
        return genotype;
    }

    public void replaceGenotype(Genotype<T> newGenotype) {
        this.genotype = newGenotype;
    }
}
