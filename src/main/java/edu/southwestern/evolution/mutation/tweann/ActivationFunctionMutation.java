package edu.southwestern.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Provides a random activation function for mutation in TWEANN class
 *
 */
public class ActivationFunctionMutation extends TWEANNMutation {

	/**
	 * Constructor
	 */
	public ActivationFunctionMutation() {
		// command line parameter that controls rate of mutation using activation functions
		super("netChangeActivationRate");
	}

	/**
	 * Mutates TWEANNGenotype using activation functions
	 */
	@Override
	public void mutate(Genotype<TWEANN> genotype) {
		TWEANNGenotype g = (TWEANNGenotype) genotype;
		ArrayList<NodeGene> nodes = g.nodes;
		NodeGene node = RandomNumbers.randomElement(nodes);
		node.ftype = ActivationFunctions.randomFunction();
	}

}
