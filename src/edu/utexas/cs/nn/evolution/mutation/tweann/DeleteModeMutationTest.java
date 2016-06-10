package edu.utexas.cs.nn.evolution.mutation.tweann;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;

public class DeleteModeMutationTest {

	TWEANNGenotype tg1;
	TWEANNGenotype tg2;
	
	@Before
	public void setUp() throws Exception {Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "mmdRate:0.1", "deleteLinkRate:0.0" });
	MMNEAT.loadClasses();
	tg1 = new TWEANNGenotype(5, 3, 0);
	MMNEAT.genotype = tg1.copy();
	EvolutionaryHistory.initArchetype(0);

	final int MUTATIONS1 = 30;

	for (int i = 0; i < MUTATIONS1; i++) {
		tg1.mutate();
	}
	tg2 = (TWEANNGenotype) tg1.copy();
	tg1.moduleDuplication();
	tg1.moduleDuplication();
	}

	@After
	public void tearDown() throws Exception {	
	MMNEAT.clearClasses();
	tg1 = null;
	tg2 = null;
	}

	@Test
	public void test() {
		System.out.println("no longer a useful mutation");
	}

}
