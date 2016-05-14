package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;

/**
 * Add new synaptic link to a TWEANN
 * 
 * @author Jacob Schrum
 */
public class NewLinkMutation extends TWEANNMutation {

    public NewLinkMutation() {
        super("netLinkRate");
    }

    /**
     * Add a synaptic links between two existing nodes.
     * Potentially cull across several offspring as well.
     * @param genotype TWEANNGenotype to mutate
     */
    @Override
    public void mutate(Genotype<TWEANN> genotype) {
        ((TWEANNGenotype) genotype).linkMutation();
        cullForBestWeight((TWEANNGenotype) genotype, new int[]{1});
    }
}
