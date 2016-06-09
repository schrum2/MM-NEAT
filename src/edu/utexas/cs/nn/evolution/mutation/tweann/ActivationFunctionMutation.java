package edu.utexas.cs.nn.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 * Provides a random activation function for mutation in TWEANN class
 * 
 * @author gillespl
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
