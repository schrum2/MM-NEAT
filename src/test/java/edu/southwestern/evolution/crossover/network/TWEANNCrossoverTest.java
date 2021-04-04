package edu.southwestern.evolution.crossover.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.TWEANNGenotype;
import edu.southwestern.evolution.genotypes.TWEANNGenotype.NodeGene;
import edu.southwestern.parameters.Parameters;

/**
 * This is a JUnit test for the TWEANN crossover. It tests both upper and lower
 * boundary for the crossExcessRate
 * 
 * @author gillespl
 *
 */
public class TWEANNCrossoverTest {

	private static final int NUM_MUTATION_ITERATIONS = 100;
	private static final int DEFAULT_NUMIN = 5;
	private static final int DEFAULT_NUMOUT = 3;
	TWEANNGenotype m, f;

	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
		m = null;
		f = null;
	}
	
	
	/**
	 * Sets crossExcessRate = 1.0 so offspring inherit all innovation numbers
	 * from both parents Essentially assures maximum crossover occurs.
	 */
	@Test
	public void test_Upper_Boundary() {
		// crossExcessRate is maximized to 100%
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "connectToInputs:false",
				"crossExcessRate:1.0", "mating:true", "randomSeed:0" });
		MMNEAT.loadClasses();
		TWEANNGenotype m = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, true, 1, 1, 0);
		for (int i = 0; i < NUM_MUTATION_ITERATIONS; i++) {// performs some
															// mutations on m
			m.linkMutation();
			m.spliceMutation();
			m.linkMutation();
			m.weightMutation();
			;
		}
		TWEANNGenotype f = (TWEANNGenotype) m.copy();// copies m. Does so after
														// some mutation of m so
														// m is an ancestor
		for (int i = 0; i < NUM_MUTATION_ITERATIONS; i++) {// performs mutations
															// on both m and f
			m.linkMutation();
			m.spliceMutation();
			m.linkMutation();
			m.weightMutation();
			f.linkMutation();
			f.spliceMutation();
			f.linkMutation();
			f.weightMutation();
		}

		TWEANNGenotype originalM = (TWEANNGenotype) m.copy();
		TWEANNGenotype originalF = (TWEANNGenotype) f.copy();
		assertTrue(m.numIn == f.numIn && m.neuronsPerModule == f.neuronsPerModule);
		TWEANNGenotype o = (TWEANNGenotype) m.crossover(f);
		assertTrue(o.numIn == DEFAULT_NUMIN);
		assertTrue(o.numOut == DEFAULT_NUMOUT);
		assertTrue(m.numIn == DEFAULT_NUMIN);
		assertTrue(m.numOut == DEFAULT_NUMOUT);

		// assertTrue(TWEANNGenotype.sameStructure(originalF, f));
		// assertFalse(TWEANNGenotype.sameStructure(originalM, m));
		// assertFalse(TWEANNGenotype.sameStructure(f, o));
		ArrayList<NodeGene> mGenotype = m.nodes;// creates long arrays to
												// facilitate utilization a
												// helper method
		long[] mInno = new long[mGenotype.size()];
		for (int i = 0; i < mGenotype.size(); i++) {
			mInno[i] = mGenotype.get(i).innovation;
		}
		ArrayList<NodeGene> oGenotype = o.nodes;// creates long arrays to
												// facilitate utilization a
												// helper method
		long[] oInno = new long[oGenotype.size()];
		for (int i = 0; i < oGenotype.size(); i++) {
			oInno[i] = oGenotype.get(i).innovation;
		}
		ArrayList<NodeGene> originalFGenotype = originalF.nodes;// creates long
																// arrays to
																// facilitate
																// utilization a
																// helper method
		long[] originalFInno = new long[originalFGenotype.size()];
		for (int i = 0; i < originalFGenotype.size(); i++) {
			originalFInno[i] = originalFGenotype.get(i).innovation;
		}
		ArrayList<NodeGene> originialMGenotype = originalM.nodes;// creates long
																	// arrays to
																	// facilitate
																	// utilization
																	// a helper
																	// method
		long[] originalMInno = new long[originialMGenotype.size()];
		for (int i = 0; i < originialMGenotype.size(); i++) {
			originalMInno[i] = originialMGenotype.get(i).innovation;
		}
		// iterates through parent innovation numbers and asserts both offspring have them
		for (int i = 0; i < originalFInno.length; i++) {
			assertTrue(ArrayUtils.contains(mInno, originalFInno[i]));
			assertTrue(ArrayUtils.contains(oInno, originalFInno[i]));
		}
		// iterates through parent innovation numbers and asserts both offspring have them
		for (int i = 0; i < originalMInno.length; i++) {
			assertTrue(ArrayUtils.contains(oInno, originalMInno[i]));
			assertTrue(ArrayUtils.contains(mInno, originalMInno[i]));
		}

	}

	/**
	 * Set crossExcessRate to 0.0 so neither offspring inherits innovation
	 * numbers from the other parent. Essentially assures no crossover occurs.
	 */
	@Test
	public void test_Lower_Boundary() {
		// crossExcessRate is minimized to 0.0%
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "connectToInputs:false",
				"crossExcessRate:0.0", "mating:true" });
		MMNEAT.loadClasses();
		TWEANNGenotype m = new TWEANNGenotype(MMNEAT.networkInputs, MMNEAT.networkOutputs, true, 1, 1, 0);
		TWEANNGenotype f = (TWEANNGenotype) m.copy();// assures f has same
														// architecture and
														// structure but not
														// ancestors
		for (int i = 0; i < NUM_MUTATION_ITERATIONS; i++) {// mutates both
															// parents
															// iteratively
			m.linkMutation();
			m.spliceMutation();
			m.linkMutation();
			m.weightMutation();
			f.linkMutation();
			f.spliceMutation();
			f.linkMutation();
			f.weightMutation();
		}

		TWEANNGenotype originalM = (TWEANNGenotype) m.copy();// copies both
																// parents
																// before
																// crossover
		TWEANNGenotype originalF = (TWEANNGenotype) f.copy();
		assertTrue(m.numIn == f.numIn && m.neuronsPerModule == f.neuronsPerModule);
		TWEANNGenotype o = (TWEANNGenotype) m.crossover(f);
		assertTrue(o.numIn == DEFAULT_NUMIN);
		assertTrue(o.numOut == DEFAULT_NUMOUT);
		assertTrue(m.numIn == DEFAULT_NUMIN);
		assertTrue(m.numOut == DEFAULT_NUMOUT);

		ArrayList<NodeGene> mGenotype = m.nodes;// creates long arrays to
												// facilitate utilization a
												// helper method
		long[] mInno = new long[mGenotype.size()];
		for (int i = 0; i < mGenotype.size(); i++) {
			mInno[i] = mGenotype.get(i).innovation;
		}
		ArrayList<NodeGene> oGenotype = o.nodes;// creates long arrays to
												// facilitate utilization a
												// helper method
		long[] oInno = new long[oGenotype.size()];
		for (int i = 0; i < oGenotype.size(); i++) {
			oInno[i] = oGenotype.get(i).innovation;
		}
		// creates long arrays to facilitate utilization a helper method
		ArrayList<NodeGene> originalFGenotype = originalF.nodes;
		long[] originalFInno = new long[originalFGenotype.size()];
		for (int i = 0; i < originalFGenotype.size(); i++) {
			originalFInno[i] = originalFGenotype.get(i).innovation;
		}
		// creates long arrays to facilitate utilization a helper method
		ArrayList<NodeGene> originialMGenotype = originalM.nodes;
		long[] originalMInno = new long[originialMGenotype.size()];
		for (int i = 0; i < originialMGenotype.size(); i++) {
			originalMInno[i] = originialMGenotype.get(i).innovation;
		}

		// iterates through parent innovation numbers to assert only one child inherited them
		for (int i = 0; i < originalFInno.length; i++) {
			if (originalFInno[i] > 0) {
				assertFalse(ArrayUtils.contains(mInno, originalFInno[i]));
				assertTrue(ArrayUtils.contains(oInno, originalFInno[i]));
			}
		}
		// iterates through parent innovation numbers to assert only one child inherited them
		for (int i = 0; i < originalMInno.length; i++) {
			if (originalMInno[i] > 0) {
				assertFalse(ArrayUtils.contains(oInno, originalMInno[i]));
				assertTrue(ArrayUtils.contains(mInno, originalMInno[i]));
			}
		}
	}

}
