package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.CommonConstants;

/**
 * Module Mutation Random: Creates a new module with random incoming link
 * weights from random sources in the network.
 * 
 * @author Jacob Schrum
 */
public class MMR extends ModuleMutation {

	/**
	 * Default Constructor
	 */
	public MMR() {
		//command line parameter, "Mutation rate for adding a new network mode (MM(R) for random)"
		super("mmrRate");
	}

	/**
	 * Adds a new module. Mutates it then adds it to either the previous module or 
	 * to the inputs depending on fs parameter
	 */
	@Override
	public void addModule(TWEANNGenotype genotype) {
		int linksAdded = genotype.moduleMutation(true, 
				CommonConstants.fs ? CommonConstants.fsLinksPerOut : genotype.numIn);
		int[] subs = new int[linksAdded];
		for (int i = 0; i < linksAdded; i++) { //adds ascending integers into an array that is the size of the amount of links added
			subs[i] = i + 1;
		}
		cullForBestWeight(genotype, subs);
	}
}
