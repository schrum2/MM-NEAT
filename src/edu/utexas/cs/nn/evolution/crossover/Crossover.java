package edu.utexas.cs.nn.evolution.crossover;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 *
 * @author Jacob Schrum
 * This is an abstract class that takes in the genotypes of two agents, crosses them over,
 * then returns the final product.
 */
public abstract class Crossover<T> {

	/**
	 * 
	 * @param toModify: Reference to genotype that is modified by crossover.
	 * @param toReturn:Reference to genotype that is returned unmodified.
	 * @return: Returns the second genotype produced by the crossover.
	 */
    public abstract Genotype<T> crossover(Genotype<T> toModify, Genotype<T> toReturn);

    /**
     * 
     * @param par1: The pair to be swapped with par2.
     * @param par2:The pair to be swapped with par1.
     * @return: a new pair with par1 in the par2 spot and par2 in the par1 spot. 
     */
    public static <G> Pair<G, G> swap(G par1, G par2) {
        return new Pair<G, G>(par2, par1);
    }
}
