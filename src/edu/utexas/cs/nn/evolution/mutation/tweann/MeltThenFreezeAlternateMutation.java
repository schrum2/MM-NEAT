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
public class MeltThenFreezeAlternateMutation extends TWEANNMutation {

    public MeltThenFreezeAlternateMutation() {
        super("freezeAlternateRate");
    }

    public void mutate(Genotype<TWEANN> genotype) {
        ((TWEANNGenotype) genotype).alternateFrozenPreferencePolicy();
    }
}
