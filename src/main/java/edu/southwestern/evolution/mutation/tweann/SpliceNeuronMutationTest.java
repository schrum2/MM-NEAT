package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.TWEANN.Node;
import edu.southwestern.parameters.Parameters;

public class SpliceNeuronMutationTest {

	TWEANNGenotype tg1;
	int MUTATION_NUM = 20;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false","recurrency:false", "perLinkMutateRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype();
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests that mutation properly occurs by splitting a neuron
	 */
	@Test
	public void test() {
		ArrayList<NodeGene> nodes = tg1.nodes;
		System.out.println("tg1: " + nodes);
		SpliceNeuronMutation SNM = new SpliceNeuronMutation();
		double[] originalInnovations = new double[nodes.size()];
		int[] originalNodeTypes = new int[nodes.size()];
		System.out.println("  Original: " + nodes.size());
		//asserts that all original nodes are correct
		for(int i = 0; i < nodes.size(); i++) {
			assertTrue(nodes.get(i).ntype != Node.NTYPE_HIDDEN);
			originalNodeTypes[i] = nodes.get(i).ntype;
			originalInnovations[i] = nodes.get(i).innovation;
			System.out.println("	Innovation at " + i + " is " + nodes.get(i).innovation);
		}
		
		SNM.mutate(tg1); //split neuron mutation
		double[] mutatedInnovations = new double[nodes.size()];
		int[] mutatedNodeTypes = new int[nodes.size()];
		System.out.println("  Mutated: " + nodes.size());
		//asserts that all mutated nodes are correct
		for(int j = 0; j < nodes.size(); j++) {
			mutatedNodeTypes[j] = nodes.get(j).ntype;
			mutatedInnovations[j] = nodes.get(j).innovation;
			System.out.println("	Innovation at " + j + " is " + nodes.get(j).innovation);
		}
		
		assertEquals(originalInnovations.length+1, mutatedInnovations.length); //mutated innovation should be one unit shorter than original innovation 
		assertFalse(originalInnovations.equals(mutatedInnovations)); //asserts that mutation has occurred
		int buffer = 1;
		for(int n = 0; n < buffer; n++){
			if(originalNodeTypes[n] != mutatedNodeTypes[n]){
				assertEquals(mutatedNodeTypes[n], Node.NTYPE_HIDDEN); //assert that mutated nodes are of hidden type
			} else {
				buffer++;
			}
		}
	}

}
