/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

/**
 *
 * @author Jacob Schrum
 */
public class WeightPurturbationMutation extends TWEANNMutation {

	/**
	 * Default constructor
	 */
	public WeightPurturbationMutation() {
		//command line parameter, "Mutation rate for network weight perturbation"
		super("netPerturbRate");
	}

	/**
	 * mutates genotype by mutating perturbed weights
	 * @param gentoype TWEANNGenotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).weightMutation();
	}
}
