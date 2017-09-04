package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

/**
 *Mutation that splices a new neuron into network
 * @author Jacob Schrum
 */
public class SpliceNeuronMutation extends TWEANNMutation {

	/**
	 * Default constructor
	 * @param netSpliceRate command line parameter that
	 * determines rate of network splices during this
	 * mutation
	 */
	public SpliceNeuronMutation() {
		// command line parameter, "Mutation rate for splicing of new network nodes"
		super("netSpliceRate");
	}

	/**
	 * Mutates genotype by splicing in a new neuron
	 * @param genotype TWEANNGenotype to be mutated
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).spliceMutation();
		cullForBestWeight((TWEANNGenotype) genotype, new int[] { 2 });
	}
}
