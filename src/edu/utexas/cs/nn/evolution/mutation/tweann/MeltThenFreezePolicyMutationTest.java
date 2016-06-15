package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.utexas.cs.nn.parameters.Parameters;

public class MeltThenFreezePolicyMutationTest {

	TWEANNGenotype tg1;
	MeltThenFreezePolicyMutation mtfpm;
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezePolicyRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		mtfpm = new MeltThenFreezePolicyMutation();
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		mtfpm = null;
		MMNEAT.clearClasses();
	}

	@Test
	public void test() {
		mtfpm.mutate(tg1);
		int x = 0;
		for(NodeGene node : tg1.nodes) {
			if(node.frozen) x++;
		}
		assertTrue(x != 0);
	}

}
