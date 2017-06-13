package edu.utexas.cs.nn.evolution.genotypes;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;

public class OffsetHybrIDGenotypeTest {
	HyperNEATCPPNGenotype hncg;
	OffsetHybrIDGenotype ohg;
	
	@Before
	public void setUp() throws Exception {
		hncg = new HyperNEATCPPNGenotype();
		ohg = new OffsetHybrIDGenotype(hncg.links, hncg.nodes, hncg.neuronsPerModule);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAllOffsetsZero() {
		RealValuedGenotype rvg = new RealValuedGenotype(HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()));
		for(int i = 0; i < HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()); i++) {
			rvg.setValue(i, 0.0);
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
