package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.networks.TWEANN;

/**
 * Delete link and then make new link with same weight pointing somewhere else,
 * effectively redirecting the link.
 *
 * @author Jacob Schrum
 */
public class RedirectLinkMutation extends TWEANNMutation {

	/**
	 * default constructor
	 */
	public RedirectLinkMutation() {
		//command line parameter, "Mutation rate for redirecting network links"
		super("redirectLinkRate");
	}

	/**
	 * mutates given genotype by deleting a link and adding a new 
	 * link connecting to a different node than deleted link
	 * @param genotype TWEANNGenotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		LinkGene lg = ((TWEANNGenotype) genotype).deleteLinkMutation();
		((TWEANNGenotype) genotype).linkMutation(lg.sourceInnovation, lg.weight);
	}
}
