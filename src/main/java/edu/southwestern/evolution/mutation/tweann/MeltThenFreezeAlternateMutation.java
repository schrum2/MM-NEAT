package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

/**
 *Mutation that alternates melting and mutating with freezing the
 *preference neuron
 * @author Jacob Schrum
 */
public class MeltThenFreezeAlternateMutation extends TWEANNMutation {

	/**
	 * default constructor
	 */
	public MeltThenFreezeAlternateMutation() {
		//command line parameter, "Mutation rate for melting all then freezing policy or preference neurons (alternating)"
		super("freezeAlternateRate");
	}

	/**
	 * Mutate method using MFAM alternation
	 * @param genotype TWEANNGenotype to mutate
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).alternateFrozenPreferencePolicy();
	}
}
