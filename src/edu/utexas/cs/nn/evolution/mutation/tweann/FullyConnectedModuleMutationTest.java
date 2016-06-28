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
import edu.utexas.cs.nn.parameters.Parameters;

public class FullyConnectedModuleMutationTest {

	TWEANNGenotype tg1;
	TWEANNGenotype tg2;
	FullyConnectedModuleMutation fcmm;
	
	@Before
	public void setUp() throws Exception {Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "fullMMRate:1.0" });
	MMNEAT.loadClasses();
	tg1 = new TWEANNGenotype(5, 3, 0);
	MMNEAT.genotype = tg1.copy();
	EvolutionaryHistory.initArchetype(0);
	fcmm = new FullyConnectedModuleMutation();
	final int MUTATIONS1 = 30;

	for (int i = 0; i < MUTATIONS1; i++) {
		tg1.mutate();
	}
	tg1.moduleDuplication();
	tg1.moduleDuplication();
	tg2 = (TWEANNGenotype) tg1.copy();
	}
	
	@After
	public void tearDown() {
		tg1 = null;
		tg2 = null;
		fcmm = null;
		MMNEAT.clearClasses();
	}
	
	@Test
	public void testMutate() {
		fcmm.mutate(tg1);
		assertFalse(tg1.links.equals(tg2.links));
	}

	@Test
	public void testAddModule() {
		ArrayList<LinkGene> links = tg1.links;
		fcmm.addModule(tg1);
		assertFalse(tg1.numModules == tg2.numModules());
		assertEquals(links.size(), tg1.links.size());
		assertTrue(links.equals(tg1.links));
	}
}
