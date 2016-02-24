/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 *
 * @author Jacob Schrum
 */
public class FullyConnectedModeMutation extends ModuleMutation {

    public FullyConnectedModeMutation() {
        super("fullMMRate");
    }

    @Override
    public boolean perform() {
        return !CommonConstants.fs && super.perform();
    }

    @Override
    public void addMode(TWEANNGenotype genotype) {
        int linksAdded = genotype.fullyConnectedModeMutation();
        int[] subs = new int[linksAdded];
        for (int i = 0; i < linksAdded; i++) {
            subs[i] = i + 1;
        }
        cullForBestWeight(genotype, subs);
    }
}
