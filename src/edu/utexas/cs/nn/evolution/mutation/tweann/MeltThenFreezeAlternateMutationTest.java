package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;

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

	@Test
	public void test() {
		tg1.insertPreferenceNeuron(0);
		for(int i = 0; i < tg1.nodes.size(); i++) {
		assertFalse(tg1.nodes.get(i).frozen);
		}
		tg1.freezePreferenceNeurons();
		int firstPreference = tg1.outputStartIndex() + tg1.neuronsPerModule;
		mtfam.mutate(tg1);
		for(int i = 0; i < firstPreference; i++) {
			assertTrue(tg1.nodes.get(i).frozen);
		}
		
	}

}
