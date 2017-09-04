package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.random.RandomNumbers;

public class OffsetHybrIDGenotypeTest {
	HyperNEATCPPNGenotype hncg;
	OffsetHybrIDGenotype ohg;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", 
				"task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask","linkExpressionThreshold:-1"});
		MMNEAT.loadClasses();
		hncg = new HyperNEATCPPNGenotype();
		ohg = new OffsetHybrIDGenotype(hncg.archetypeIndex, hncg.links, hncg.nodes, hncg.neuronsPerModule);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAllOffsetsZero() {
		for(int i = 0; i < HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()); i++) {
			ohg.rvg.setValue(i, 0.0);
		}
		
		// Loop 100 times
		// Generate random input arrays: RandomNumbers.randomArray(size)
		// Send input array to both networks: use process to send input and get output
		// assertArrayEquals on output arrays
		
		for(int i = 0; i < 100; i++) {
			double[] randomArray = RandomNumbers.randomArray(hncg.getPhenotype().numInputs());
			double[] hncgOutput = hncg.getPhenotype().process(randomArray);
			double[] ohgOutput = ohg.getPhenotype().process(randomArray);
			assertTrue(Arrays.equals(hncgOutput, ohgOutput));
		}
	}
	
	@Test
	public void testLinkSize() {
		assertEquals(ohg.links.size(), hncg.links.size());
		assertEquals(ohg.rvg.getPhenotype().size(), ((TWEANNGenotype) hncg.getSubstrateGenotype(HyperNEATUtil.getHyperNEATTask())).links.size());
	}
}
