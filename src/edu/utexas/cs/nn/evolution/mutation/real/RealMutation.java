/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.real;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.evolution.mutation.Mutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.parameters.Parameters;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class RealMutation extends Mutation<ArrayList<Double>> {

    protected final double rate;

    public RealMutation() {
        this.rate = Parameters.parameters.doubleParameter("realMutateRate");
    }

    /*
     * Each index is checked to see if mutation should be performed
     */
    @Override
    public boolean perform() {
        return RandomNumbers.randomGenerator.nextDouble() <= rate;
    }

    @Override
    public void mutate(Genotype<ArrayList<Double>> genotype) {
        for (int i = 0; i < genotype.getPhenotype().size(); i++) {
            if (perform()) {
                mutateIndex((RealValuedGenotype) genotype, i);
            }
        }
    }

    public abstract void mutateIndex(RealValuedGenotype genotype, int i);
}
