package edu.utexas.cs.nn.evolution.mutation;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;

/**
 *
 * @author He_Deceives
 */
public abstract class Mutation<T> {

    public StringBuilder infoTracking = null;

    public boolean go(Genotype<T> genotype, StringBuilder infoTracking) {
        this.infoTracking = infoTracking;
        if (perform()) {
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
     * @return
     */
    public abstract boolean perform();

    /**
     * Modifies the genotype
     *
     * @param genotype to modify
     */
    public abstract void mutate(Genotype<T> genotype);
}
