package edu.utexas.cs.nn.evolution.mutation.tweann;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;

/**
 * mutation that melts network and freezes only policy neuron
 * @author Jacob Schrum
 */
public class MeltThenFreezePolicyMutation extends TWEANNMutation {

	/**
	 * default constructor
	 */
	public MeltThenFreezePolicyMutation() {
		//command line parameter, "Mutation rate for melting all then freezing policy neurons"
		super("freezePolicyRate");
	}

	/**
	 * mutates genotype using MFPM 
	 * @param genotype TWEANNGenotype to mutate
	 */
	public void mutate(Genotype<TWEANN> genotype) {
		((TWEANNGenotype) genotype).meltNetwork();//melts network
		((TWEANNGenotype) genotype).freezePolicyNeurons();//freezes policy neuron
	}
}
