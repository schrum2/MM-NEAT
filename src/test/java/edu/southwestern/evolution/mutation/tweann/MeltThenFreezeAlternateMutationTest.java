package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.parameters.Parameters;

public class MeltThenFreezeAlternateMutationTest {

	TWEANNGenotype tg1;
	MeltThenFreezeAlternateMutation mtfam;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezeAlternateRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		mtfam = new MeltThenFreezeAlternateMutation();
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		mtfam = null;
		MMNEAT.clearClasses();
	}
	
	/**
	 * Loops through TWEANNGenotype and tests whether the data at the nodes has been frozen or not based on
	 * the state of the preference neurons
	 */
	@Test
	public void test() {
		tg1.insertPreferenceNeuron(0); //not frozen
		for(int i = 0; i < tg1.nodes.size(); i++) {
		assertFalse(tg1.nodes.get(i).isFrozen());
		}
		
		// The freezePreferenceNeurons method complains (assertion error) if it thinks
		// now preference neurons can possibly exist, so here the mmpRate is set above 0
		// to fool the assertion check.
		Parameters.parameters.setDouble("mmpRate", 0.000000000000000000001);
		tg1.freezePreferenceNeurons(); //frozen
		int firstPreference = tg1.outputStartIndex() + tg1.neuronsPerModule;
		mtfam.mutate(tg1);
		// Disable Module Mutation again
		Parameters.parameters.setDouble("mmpRate", 0.0);
		for(int i = 0; i < firstPreference; i++) {
			assertTrue(tg1.nodes.get(i).isFrozen());
		}
		
	}

}
