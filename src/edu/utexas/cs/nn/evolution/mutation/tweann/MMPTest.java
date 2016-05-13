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
		Parameters.initializeParameterCollections(new String[]{"io:false","recurrency:false","mmdRate:0.1"});
        MMNEAT.loadClasses();
        tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		tg2 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
        MMNEAT.genotype = tg1.copy();
        EvolutionaryHistory.initArchetype(0);
        
        for (int i = 0; i < MUTATIONS1; i++) {
            tg1.mutate();
            tg2.mutate();
        }
	}

	@Test
	public void test_modeMutation() {
		mmp1 = new MMP();
		
	}

}
