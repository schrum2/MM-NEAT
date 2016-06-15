package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.parameters.Parameters;

public class MeltThenFreezePreferenceMutationTest {

	TWEANNGenotype tg1;
	MeltThenFreezePreferenceMutation mtfpm;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "freezePreferenceRate:1.0"});
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		mtfpm = new MeltThenFreezePreferenceMutation();
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
		int firstPreference = tg1.outputStartIndex() + tg1.neuronsPerModule;
		for(int i = 0; i < firstPreference; i++) {
			assertFalse(tg1.nodes.get(i).frozen);
		}
		assertTrue(tg1.nodes.get(firstPreference).frozen);
	}

}
