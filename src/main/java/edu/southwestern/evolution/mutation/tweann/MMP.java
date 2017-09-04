package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;

/**
 *Module Mutation Previous
 *Adds a new mutated module to network 
 *linked to previous module
 * @author Jacob Schrum
 */
public class MMP extends ModuleMutation {

	/**
	 * default constructor
	 */
	public MMP() {
		//command line parameter, "Mutation rate for adding a new network mode (MM(P) for previous)"
		super("mmpRate");
	}

	/**
	 * adds a new mutated module
	 */
	@Override
	public void addModule(TWEANNGenotype genotype) {
		genotype.moduleMutation(false, 1);
	}

	
}
