/**
 * 
 */
package edu.utexas.cs.nn.evolution.mutation.tweann;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.EvolutionaryHistory;
import edu.utexas.cs.nn.evolution.crossover.network.TWEANNCrossover;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 * Tests Module mutation duplication methods
 * 
 * @author gillespl
 *
 */
public class MMDTest {

	final int MUTATIONS1 = 30;//number of mutations that occur
	public static final int SIZE = 20;//number of iterations of test
	
	TWEANNGenotype tg1, tg2;//makes the TWEANNS global so test can access them
	
	/**
	 * Sends correct parameters to the command line, loads classes
	 * and sets up necessary TWEANNS
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false","recurrency:false","mmdRate:1.0"});
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

/**
 * Tests whether mode duplication method works	
 */
	@Test
	public void test_modeDuplication() {
		//test 1
		tg1.modeDuplication();
		TWEANN t1 = tg1.getPhenotype();
		// This loop confirms that the new mode is a duplicate of the previous
		for(int i = 0; i < SIZE; i++) {
			double[] in = RandomNumbers.randomArray(t1.numInputs());
			t1.process(in);
			Assert.assertArrayEquals(t1.modeOutput(0),t1.modeOutput(1), i);
		}

		//test 2
		tg2.modeDuplication();
		TWEANNCrossover cross = new TWEANNCrossover();
		TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
		TWEANN t2 = new2.getPhenotype();

		for(int i = 0; i < SIZE; i++) {
			double[] in = RandomNumbers.randomArray(t2.numInputs());
			t2.process(in);
			assertFalse(t2.modeOutput(0).equals(t2.modeOutput(1)));
		}
	
		//test 3
		TWEANN t3 = tg1.getPhenotype();
		for(int i = 0; i < 20; i++) {
			double[] in = RandomNumbers.randomArray(t3.numInputs());
			t3.process(in);
			assertFalse(t3.modeOutput(0).equals(t3.modeOutput(1)));
		}
	}

}

