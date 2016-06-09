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
 *Mutates all weights in a TWEANN
 * @author Jacob Schrum
 */
public class AllWeightMutation extends TWEANNMutation {

	   //random number generator and mutation rate
	  //not actually used to mutate links
	private final RandomGenerator rand;
	private final double perLinkMutateRate;

	/**
	 * Default Constructor
	 */
	public AllWeightMutation() {
		this(MMNEAT.weightPerturber, CommonConstants.perLinkMutateRate);
	}

	/**
	 * mutates all weights
	 * @param rand random # generator
	 * @param perLinkMutateRate mutation rate of links (parameter)
	 */
	public AllWeightMutation(RandomGenerator rand, double perLinkMutateRate) {
		// Always execute this mutation, since the randomness comes in on a per
		// link basis
		super(1.0);
		this.perLinkMutateRate = perLinkMutateRate;
		this.rand = rand;
	}

	/**
	 * Mutates all weights in a TWEANNGenotype
	 * @param genotype TWEANNGenotype to be mutated
	 */
	@Override
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).allWeightMutation(rand, perLinkMutateRate);
	}
}
