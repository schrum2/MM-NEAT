package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;

public class MMPTest {

	TWEANNGenotype tg1, tg2;
	MMP mmp1;
	final int MUTATIONS1 = 30;

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false","netio:false", "recurrency:false","mmpRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		mutate(tg1);
	}

	public void mutate(TWEANNGenotype tg1) {
		for (int i = 0; i < MUTATIONS1; i++) {
			tg1.mutate();
		}
	}

	@Test
	public void test_moduleMutation() {
		tg2 = (TWEANNGenotype) tg1.copy();
		new MMP().mutate(tg1);
		assertTrue(tg1.numModules > 1);
		//tg1.
		
		
		
		
		
		
		
		
//		//test 1. Tests if moduleMutation method changes number of modules
//		tg2 = (TWEANNGenotype) tg1.copy();
//		int numLinksTryingToAdd = 1;
//		int numLinksAdded = tg1.moduleMutation(false, numLinksTryingToAdd);
//		assertTrue(tg1.numModules != tg2.numModules);
//		assertFalse(numLinksAdded == numLinksTryingToAdd);
//		new MMP().mutate(tg2);
//		assertFalse(TWEANNGenotype.sameStructure(tg2, tg1));
//
//
//		//test 2
//		mutate(tg1);
//		mutate(tg2);
//		TWEANNCrossover cross = new TWEANNCrossover();
//		TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
//		assertFalse(TWEANNGenotype.sameStructure(tg1, new2));

		
	}

}
