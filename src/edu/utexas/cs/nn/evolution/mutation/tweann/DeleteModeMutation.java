/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 *
 * @author Jacob Schrum
 */
public class DeleteModeMutation extends TWEANNMutation {

    public DeleteModeMutation() {
        super("deleteModeRate");
    }

    public void mutate(Genotype<TWEANN> genotype) {
        if (CommonConstants.deleteLeastUsed) {
            ((TWEANNGenotype) genotype).deleteLeastUsedModeMutation();
        } else {
            ((TWEANNGenotype) genotype).deleteRandomModeMutation();
        }
    }
}
