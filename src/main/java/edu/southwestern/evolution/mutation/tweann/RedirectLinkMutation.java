package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.southwestern.networks.TWEANN;

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
		LinkGene lg = ((TWEANNGenotype) genotype).deleteLinkMutation(); //deletes link
		if(lg != null) //accounts for deleted link potentially emptying LinkGene
			((TWEANNGenotype) genotype).linkMutation(lg.sourceInnovation, lg.weight);
	}
}
