/**
 * 
 */
package edu.southwestern.evolution.mutation.tweann;

import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.EvolutionaryHistory;
import edu.southwestern.evolution.crossover.network.TWEANNCrossover;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Tests Module mutation duplication methods
 * 
 * @author gillespl
 *
 */
public class MMDTest {

	final int MUTATIONS1 = 30;// number of mutations that occur
	public static final int SIZE = 20;// number of iterations of test

	TWEANNGenotype tg1, tg2;// makes the TWEANNS global so test can access them

	/**
	 * Sends correct parameters to the command line, loads classes and sets up
	 * necessary TWEANNS
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(
				new String[] { "io:false", "netio:false", "recurrency:false", "mmdRate:1.0" });
		MMNEAT.loadClasses();
		tg1 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		tg2 = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, 0);
		MMNEAT.genotype = tg1.copy();
		EvolutionaryHistory.initArchetype(0);
		
		// These mutations change the structure, but should not add modules
		Parameters.parameters.setDouble("mmdRate", 0);
		mutate(tg1, tg2);
		Parameters.parameters.setDouble("mmdRate", 1.0); // Back to 1.0 for the following tests
	}

	@After
	public void tearDown() throws Exception {
		tg1 = null;
		tg2 = null;
		MMNEAT.clearClasses();
	}
	
	public void mutate(TWEANNGenotype tg1, TWEANNGenotype tg2) {
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
		// test 1
		tg1.moduleDuplication();
		TWEANN t1 = tg1.getPhenotype();
		
		// This loop confirms that the new mode is a duplicate of the previous
		for (int i = 0; i < SIZE; i++) {
			double[] in = RandomNumbers.randomArray(t1.numInputs());
			t1.process(in);
			Assert.assertArrayEquals(t1.moduleOutput(0), t1.moduleOutput(1), i);
		}

		// test 2
		tg2.moduleDuplication();
		TWEANNCrossover cross = new TWEANNCrossover();
		TWEANNGenotype new2 = (TWEANNGenotype) cross.crossover(tg1, tg2);
		TWEANN t2 = new2.getPhenotype();

		for (int i = 0; i < SIZE; i++) {
			double[] in = RandomNumbers.randomArray(t2.numInputs());
			t2.process(in);
			assertFalse(t2.moduleOutput(0).equals(t2.moduleOutput(1)));
		}

		// test 3
		TWEANN t3 = tg1.getPhenotype();
		for (int i = 0; i < 20; i++) {
			double[] in = RandomNumbers.randomArray(t3.numInputs());
			t3.process(in);
			assertFalse(t3.moduleOutput(0).equals(t3.moduleOutput(1)));
		}
	}

}
