package edu.utexas.cs.nn.evolution.crossover;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 */
public abstract class Crossover<T> {
    // Send in two genotypes to cross, modify one, return the other

    public abstract Genotype<T> crossover(Genotype<T> toModify, Genotype<T> toReturn);

    public static <G> Pair<G, G> swap(G par1, G par2) {
        return new Pair<G, G>(par2, par1);
    }
}
