package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;

public class SpliceNeuronMutationTest {

	TWEANNGenotype tg1;
	int MUTATION_NUM = 20;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "recurrency:false", "perLinkMutateRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype();
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		ArrayList<NodeGene> nodes = tg1.nodes;
		System.out.println("tg1: " + nodes);
		SpliceNeuronMutation SNM = new SpliceNeuronMutation();
		double[] originalInnovations = new double[nodes.size()];
		System.out.println("  Original: " + nodes.size());
		for(int i = 0; i < nodes.size(); i++) {
			assertTrue(nodes.get(i).ntype != Node.NTYPE_HIDDEN);
			originalInnovations[i] = nodes.get(i).innovation;
			System.out.println("	Innovation at " + i + " is " + nodes.get(i).innovation);
		}
		SNM.mutate(tg1);
		double[] mutatedInnovations = new double[nodes.size()];
		System.out.println("  Mutated: " + nodes.size());
		for(int j = 0; j < nodes.size(); j++) {
			mutatedInnovations[j] = nodes.get(j).innovation;
			System.out.println("	Innovation at " + j + " is " + nodes.get(j).innovation);
		}
		
		assertEquals(originalInnovations.length+1, mutatedInnovations.length);
		assertFalse(originalInnovations.equals(mutatedInnovations));
	}

}
