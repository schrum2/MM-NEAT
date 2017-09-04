package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;

/**
 * Module Mutation Duplication
 * Simple class that allows one to duplicate a module that is identical to the
 * previous one.
 *
 * @author Jacob Schrum
 */
public class MMD extends ModuleMutation {

	/**
	 * Constructor (inherited from TWEANN mutation)
	 */
	public MMD() {
		//command line parameter, "Mutation rate for adding a new network mode (MM(D) for duplication)"
		super("mmdRate");
	}

	/**
	 * Adds a module identical to the previous genotype
	 * 
	 * @param genotype:
	 *            the genotype of the TWEANN to be added to the new module
	 */
	@Override
	public void addModule(TWEANNGenotype genotype) {
		genotype.moduleDuplication();
	}

}
