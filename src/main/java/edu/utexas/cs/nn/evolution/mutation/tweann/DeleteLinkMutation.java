package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;

/**
 * Mutation that deletes a single link from a TWEANN
 *
 * @author Jacob Schrum
 */
public class DeleteLinkMutation extends TWEANNMutation {

	/**
	 * Default constructor
	 */
	public DeleteLinkMutation() {
		//command line parameter, "Mutation rate for deleting network links"
		super("deleteLinkRate");
	}

	/**
	 * Mutates TWEANNGenotype
	 * @param genotype TWEANNGenotype to mutate
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).deleteLinkMutation();
	}

}
