/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.util.random.RandomGenerator;

/**
 *
 * @author Jacob Schrum
 */
public class AllWeightMutation extends TWEANNMutation {

    private final RandomGenerator rand;
    private final double perLinkMutateRate;

    public AllWeightMutation() {
        this(MMNEAT.weightPerturber, CommonConstants.perLinkMutateRate);
    }

    public AllWeightMutation(RandomGenerator rand, double perLinkMutateRate) {
        // Always execute this mutation, since the randomness comes in on a per link basis
        super(1.0);
        this.perLinkMutateRate = perLinkMutateRate;
        this.rand = rand;
    }

    @Override
    public void mutate(Genotype<TWEANN> genotype) {
        ((TWEANNGenotype) genotype).allWeightMutation(rand, perLinkMutateRate);
    }
}
