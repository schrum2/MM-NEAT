package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

/**
 * Add new synaptic link to a TWEANN
 * 
 * @author Jacob Schrum
 */
public class NewLinkMutation extends TWEANNMutation {

	/**
	 * Default constructor
	 */
	public NewLinkMutation() {
		// command line parameter, "Mutation rate for creation of new network synapses"
		super("netLinkRate");
	}

	/**
	 * Add a synaptic link between two existing nodes. Potentially cull across
	 * several offspring as well.
	 * 
	 * @param genotype
	 *            TWEANNGenotype to mutate
	 */
	@Override
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).linkMutation();
		cullForBestWeight((TWEANNGenotype) genotype, new int[] { 1 });
		//culls for the best weight from random link addition
	}
}
