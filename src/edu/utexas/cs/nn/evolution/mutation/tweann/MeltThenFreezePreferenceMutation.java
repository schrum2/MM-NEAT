package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;

/**
 *Mutation that melts network then freezes the policy neuron
 * @author Jacob Schrum
 */
public class MeltThenFreezePreferenceMutation extends TWEANNMutation {

	/**
	 * default constructor 
	 */
	public MeltThenFreezePreferenceMutation() {
		//command line parameter, "Mutation rate for melting all then freezing preference neurons"
		super("freezePreferenceRate");
	}

	/**
	 * mutates genotype with MFPM policy
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).meltNetwork();//melts network
		((TWEANNGenotype) genotype).freezePreferenceNeurons();//freezes policy neuron
	}
}
