/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;

/**
 *Simple class that allows one to duplicate a genotype and add it
 *to a mode.
 *
 * @author Jacob Schrum
 */
public class MMD extends ModuleMutation {

	/**
	 * Constructor (inherited from TWEANN mutation)
	 */
    public MMD() {
        super("mmdRate");
    }
    
    /**
     * Adds a duplicate genotype to mode
     * 
     * @param genotype: the genotype of the TWEANN to be added
     * to the mode
     */
    @Override
    public void addMode(TWEANNGenotype genotype) {
        genotype.modeDuplication();
    }

	
	
}
