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

    public RedirectLinkMutation() {
        super("redirectLinkRate");
    }

    public void mutate(Genotype<TWEANN> genotype) {
        LinkGene lg = ((TWEANNGenotype) genotype).deleteLinkMutation();
        ((TWEANNGenotype) genotype).linkMutation(lg.sourceInnovation, lg.weight);
    }
}
