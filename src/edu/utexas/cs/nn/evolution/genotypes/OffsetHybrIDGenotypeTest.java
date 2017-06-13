package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.parameters.Parameters;

public class OffsetHybrIDGenotypeTest {
	HyperNEATCPPNGenotype hncg;
	OffsetHybrIDGenotype ohg;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[] { "io:false", "netio:false", "recurrency:false", "hyperNEAT:true", 
				"task:edu.utexas.cs.nn.networks.hyperneat.HyperNEATDummyTask","linkExpressionThreshold:-1"});
		MMNEAT.loadClasses();
		hncg = new HyperNEATCPPNGenotype();
		ohg = new OffsetHybrIDGenotype(hncg.links, hncg.nodes, hncg.neuronsPerModule);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAllOffsetsZero() {
		for(int i = 0; i < HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()); i++) {
			ohg.rvg.setValue(i, 0.0);
		}
		assertEquals(hncg.getPhenotype(), ohg.getPhenotype());
	}
	
	@Test
	public void testLinkSize() {
		assertEquals(ohg.links.size(), hncg.links.size());
	}
	
//	@Test
//	public void testGetPhenotype() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCopy() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNewInstance() {
//		fail("Not yet implemented");
//	}

}
