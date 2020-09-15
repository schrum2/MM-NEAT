package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.networks.TWEANN.Node;
import edu.southwestern.parameters.Parameters;

public class MeltThenFreezePolicyMutationTest {//TODO

	TWEANNGenotype tg1;
	MeltThenFreezePolicyMutation mtfpm;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezePolicyRate:1.0", "mmrRate:1.0", "ranomSeed:0"});
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
		ArrayList<NodeGene> nodes = tg1.nodes;
		for(int i = 0; i < tg1.nodes.size(); i++) {
			NodeGene node = nodes.get(i);
			if(node.ntype == Node.NTYPE_INPUT || node.ntype == Node.NTYPE_HIDDEN) {
				node.freeze();
			}
		}	
		//checks that calling freeze() successfully freezes node
		for(NodeGene node : nodes) {
			if(node.ntype == Node.NTYPE_INPUT || node.ntype == Node.NTYPE_HIDDEN) {
				assertEquals(node.isFrozen(), true);
			}
		}
		//System.out.println("mtfpm is not null: " + mtfpm != null);
		System.out.println("nodes before:" + tg1.nodes.toString());
		mtfpm.mutate(tg1);
		nodes = tg1.nodes;
		int numFrozen = 0;
		for(NodeGene node : tg1.nodes) {
			if(node.isFrozen()) {
				numFrozen++;
			}
		}
		System.out.println("num outputs: " + tg1.numOut + " num policy as calculated: " + ((tg1.numIn * 2)- tg1.numModules) + " num modules: " + tg1.numModules);
		System.out.println("Nodes after:" + tg1.nodes.toString());
		assertEquals(numFrozen, tg1.nodes.size() - tg1.numModules); //checks that number of frozen nodes is accurate
	}

}
