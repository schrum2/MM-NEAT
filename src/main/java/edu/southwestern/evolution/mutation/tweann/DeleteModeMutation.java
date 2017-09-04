package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.CommonConstants;

/**
 *
 *This mutation operator currently does not work because of
 *details with how the network archetype manages innovation numbers
 *and node layout. DO NOT USE.
 *
 * @author Jacob Schrum
 */
public class DeleteModeMutation extends TWEANNMutation {

	/**
	 * Default constructor
	 */
	public DeleteModeMutation() {
		//command line parameter, "Mutation rate for deleting network modes"
		super("deleteModeRate");
		throw new UnsupportedOperationException("Mode deletion currently does not work");
	}

	/**
	 * mutates genotype by deleting least used module
	 * or a random module
	 * @param genotype TWEANNGenotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		if (CommonConstants.deleteLeastUsed) {
			((TWEANNGenotype) genotype).deleteLeastUsedModeMutation();
		} else {
			((TWEANNGenotype) genotype).deleteRandomModeMutation();
		}
	}
}
