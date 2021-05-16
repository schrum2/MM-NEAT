package edu.southwestern.evolution.mapelites;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.southwestern.parameters.Parameters;

public class MAPElitesTest {

//	@Test
//	public void testGetBinLabelsClass() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetArchive() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testInitialize() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testCalculateQDScore() {
		Parameters.initializeParameterCollections(new String[] {});
		assertEquals(0.0, MAPElites.calculateQDScore(new Float[] {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY}), 0);
		assertEquals(282.199, MAPElites.calculateQDScore(new Float[] {306.2f, Float.NEGATIVE_INFINITY, -25.001f, 1.0f, Float.NEGATIVE_INFINITY}), 0.0001);
		assertEquals(9831.57001, MAPElites.calculateQDScore(new Float[] {56.07f, -222.5f, 1.0f, 10000.00001f, -3.0f}), 0.01);
				
		Parameters.initializeParameterCollections(new String[] {"mapElitesQDBaseOffset:0.1"});
		assertEquals(0.0, MAPElites.calculateQDScore(new Float[] {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY}), 0);
		assertEquals(282.499, MAPElites.calculateQDScore(new Float[] {306.2f, Float.NEGATIVE_INFINITY, -25.001f, 1.0f, Float.NEGATIVE_INFINITY}), 0.0001);
		assertEquals(9832.07001, MAPElites.calculateQDScore(new Float[] {56.07f, -222.5f, 1.0f, 10000.00001f, -3.0f}), 0.01);
		
		Parameters.initializeParameterCollections(new String[] {"mapElitesQDBaseOffset:-0.2"});
		assertEquals(0.0, MAPElites.calculateQDScore(new Float[] {Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY}), 0);
		assertEquals(281.599, MAPElites.calculateQDScore(new Float[] {306.2f, Float.NEGATIVE_INFINITY, -25.001f, 1.0f, Float.NEGATIVE_INFINITY}), 0.0001);
		assertEquals(9830.57001, MAPElites.calculateQDScore(new Float[] {56.07f, -222.5f, 1.0f, 10000.00001f, -3.0f}), 0.01);
		

	}

//	@Test
//	public void testNewIndividual() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testFileUpdates() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCurrentIteration() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testFinalCleanup() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetPopulation() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPopulationChanged() {
//		fail("Not yet implemented");
//	}

}
