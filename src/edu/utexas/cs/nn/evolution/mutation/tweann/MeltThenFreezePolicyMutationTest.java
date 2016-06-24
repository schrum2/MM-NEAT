package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.networks.TWEANN.Node;
import edu.utexas.cs.nn.parameters.Parameters;

public class MeltThenFreezePolicyMutationTest {//TODO

	TWEANNGenotype tg1;
	MeltThenFreezePolicyMutation mtfpm;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezePolicyRate:1.0", "mmrRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		mtfpm = new MeltThenFreezePolicyMutation();
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
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
		int numPolicy = 2 * tg1.numOut;
		ArrayList<NodeGene> nodes = tg1.nodes;
		for(int i = 0; i < tg1.nodes.size(); i++) {
			NodeGene node = nodes.get(i);
			if(node.ntype == Node.NTYPE_INPUT || node.ntype == Node.NTYPE_HIDDEN) {
				node.freeze();
				assertEquals(node.frozen, true);
			}
		}
		mtfpm.mutate(tg1);
		 int numFrozen = 0;
		for(NodeGene node : nodes) {
			if(node.frozen) {
				numFrozen++;
			}
		}
		System.out.println("num outputs: " + tg1.numOut + " num policy as calculated: " + numPolicy);
		assertEquals(numFrozen, tg1.numOut);
	}

}
