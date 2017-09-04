package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;

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
		System.out.println("mtfpm " + genotype.toString());
	}
}
