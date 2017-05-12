package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.LinkGene;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.parameters.Parameters;

/**
 * JUnit test for link mutation of TWEANNGenotype
 * 
 * @author gillespl
 *
 */
public class NewLinkMutationTest {

	TWEANNGenotype tg1;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "mmdRate:1.0" });
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		System.out.println("MMNEAT JUnit default network inputs: " + MMNEAT.networkInputs
				+ " MMNEAT JUnit default network outputs: " + MMNEAT.networkOutputs);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests that adding a synaptic link between two nodes can be accomplished successfully
	 */
	@Test
	public void testLinkMutation() {
		double weight = 0.5;
		int outputNodeIndex = 5;
		int prefNodeIndex = 8;
		int hiddenNodesInno = 0;
		int numNodesPre = tg1.nodes.size();
		for (int i = outputNodeIndex; i < prefNodeIndex; i++) {
			tg1.spliceNode(0, ++hiddenNodesInno, tg1.nodes.get(0).innovation, tg1.nodes.get(i).innovation, weight,
					weight, tg1.links.size(), tg1.links.size() + 1);
		}
		assertTrue(numNodesPre != tg1.nodes.size());
		ArrayList<NodeGene> nodes = tg1.nodes;
		ArrayList<LinkGene> links = tg1.links;
		ArrayList<Long> nodeInnos = new ArrayList<Long>(nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			nodeInnos.add(nodes.get(i).innovation);
		}
		for (LinkGene lg : links) { //asserts that all links have been added properly
			assertTrue(nodeInnos.contains(lg.sourceInnovation));
			assertTrue(nodeInnos.contains(lg.targetInnovation));
		}
	}

}
