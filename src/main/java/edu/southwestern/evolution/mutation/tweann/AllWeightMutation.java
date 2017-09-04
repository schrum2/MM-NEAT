/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.util.random.RandomGenerator;

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
		//common constant, "Per link chance of weight perturbation"
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
