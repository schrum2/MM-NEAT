/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;

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
