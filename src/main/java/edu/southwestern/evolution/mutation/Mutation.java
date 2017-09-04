package edu.southwestern.evolution.mutation;

import edu.southwestern.evolution.genotypes.Genotype;

/**
 * Performs a single mutation operation on a genotype.
 * 
 * @author Jacob Schrum
 * @param <T>
 *            Phenotype of genotype being mutated
 */
public abstract class Mutation<T> {

	public StringBuilder infoTracking = null;

	/**
	 * Mutates the genotype if the perform() test passes. The perform() test is
	 * generally probabilistic.
	 * 
	 * @param genotype
	 *            will potentially be mutated
	 * @param infoTracking
	 *            Accumulates a String of information about mutation for logs
	 * @return Whether the mutation actually occurred
	 */
	public boolean go(Genotype<T> genotype, StringBuilder infoTracking) {
		this.infoTracking = infoTracking;
		if (perform()) { // Generally probabilistic
			mutate(genotype);
			infoTracking.append(this.getClass().getSimpleName());
			infoTracking.append(" ");
			return true;
		}
		return false;
	}

	/**
	 * Returns true if mutation should be performed, false otherwise
	 *
	 * @return Whether to mutate
	 */
	public abstract boolean perform();

	/**
	 * Modifies the genotype. Called if perform() test passes.
	 *
	 * @param genotype
	 *            to modify
	 */
	public abstract void mutate(Genotype<T> genotype);
}
