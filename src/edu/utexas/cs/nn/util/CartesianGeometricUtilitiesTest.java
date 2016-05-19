package edu.utexas.cs.nn.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.util.util2D.Tuple2D;

public class CartesianGeometricUtilitiesTest {

	

	@Test
	public void testCenterAndScale() {
		Tuple2D pair = new Tuple2D(1.5 , .5);
		int width = 2;
		int height = 3;
		Tuple2D scaledPair = CartesianGeometricUtilities.centerAndScale(pair, width, height);
		assertTrue(scaledPair.x >= 0 && scaledPair.y <= 1);
		Tuple2D scaledUpper = CartesianGeometricUtilities.centerAndScale(new Tuple2D(2-1, 3-1), width, height);
		Tuple2D properUpper = new Tuple2D(1,1);
		Tuple2D scaledLower = CartesianGeometricUtilities.centerAndScale(new Tuple2D(0,0), width, height);
		Tuple2D properLower = new Tuple2D(-1,-1);
		assertEquals(scaledUpper.x, properUpper.x, .001);
		assertEquals(scaledUpper.y, properUpper.y, .001);
		assertEquals(scaledLower.x, properLower.x, .001);
		assertEquals(scaledLower.y, properLower.y, .001);
	}

	@Test
	public void testShortestDistanceToLineSegmentILocated2DILocated2DILocated2D() {
		fail("Not yet implemented");
	}

	@Test
	public void testEuclideanDistance() {
		fail("Not yet implemented");
	}

	@Test
	public void testPolarToCartesian() {
		fail("Not yet implemented");
	}

	@Test
	public void testSignedAngleFromSourceHeadingToTarget() {
		fail("Not yet implemented");
	}

	@Test
	public void testSignedAngleDifferenceILocated2DILocated2D() {
		fail("Not yet implemented");
	}

	@Test
	public void testSourceHeadingTowardsTarget() {
		fail("Not yet implemented");
	}

	@Test
	public void testOnSideOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testRestrictRadians() {
		fail("Not yet implemented");
	}

}
