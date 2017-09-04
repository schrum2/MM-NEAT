package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

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
