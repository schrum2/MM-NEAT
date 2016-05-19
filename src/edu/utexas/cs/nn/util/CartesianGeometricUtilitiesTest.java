package edu.utexas.cs.nn.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.util.util2D.Tuple2D;
import edu.utexas.cs.nn.util.util2D.ILocated2D;

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
	public void testShortestDistanceToLineSegment() {
		ILocated2D bottomOfSegment = new Tuple2D(0,0);
		ILocated2D topOfSegment = new Tuple2D(0, 1);
		ILocated2D point = new Tuple2D(1, 0);
		ILocated2D otherPoint = new Tuple2D(1, 1.5);
		assertEquals(CartesianGeometricUtilities.shortestDistanceToLineSegment(point, topOfSegment, bottomOfSegment), 1.0, .0001);
		assertEquals(CartesianGeometricUtilities.shortestDistanceToLineSegment(otherPoint, topOfSegment, bottomOfSegment), 1.1180, .001);
	}

	@Test
	public void testEuclideanDistance() {
		double x = 3;
		double y = 4;
		ArrayList<Double> x1 = new ArrayList<Double>();
		ArrayList<Double> x2 = new ArrayList<Double>();
		x1.add(x);
		x1.add(0.0);
		x2.add(0.0);
		x2.add(y);
		assertEquals(5, CartesianGeometricUtilities.euclideanDistance(x1, x2), .0001);
	}

	@Test
	public void testPolarToCartesian() {
		double r = Math.sqrt(2);
		double theta = Math.PI/4.0;
		double[] cart = CartesianGeometricUtilities.polarToCartesian(r, theta);
		double[] actual = {1, 1};
		for(int i = 0; i < cart.length; i++) {
			assertEquals(cart[i], actual[i], .0001);
		}
	}

	@Test
	public void testSignedAngleFromSourceHeadingToTarget() {
		
	}

	@Test
	public void testSignedAngleDifferenceILocated2DILocated2D() {
		
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
