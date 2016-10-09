package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;

public class MeltThenFreezePreferenceMutationTest {//TODO

	TWEANNGenotype tg1;
	MeltThenFreezePreferenceMutation mtfpm;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezePreferenceRate:1.0", "mmrRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		mtfpm = new MeltThenFreezePreferenceMutation();
		MMR mmr = new MMR();
		mmr.mutate(tg1);
		assertEquals(tg1.numModules, 2);
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		mtfpm = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		for(NodeGene node : tg1.nodes) {
			if(node.ntype == Node.NTYPE_INPUT || node.ntype == Node.NTYPE_HIDDEN) {
				node.freeze();
				assertEquals(node.isFrozen(), true);
			}
		}

		System.out.println("nodes before: " + tg1.nodes.toString());
		mtfpm.mutate(tg1);
		System.out.println("nodes after: " + tg1.nodes.toString());
		int numFrozen = 0;
		for(NodeGene node : tg1.nodes) {
			if(node.isFrozen()) {
				numFrozen++;
			}
		}
		assertTrue(numFrozen == tg1.numModules +1 || numFrozen == tg1.numModules + 2);
	}

}
