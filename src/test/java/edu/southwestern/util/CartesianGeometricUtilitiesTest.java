package edu.southwestern.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import edu.southwestern.util.util2D.Tuple2D;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.util2D.ILocated2D;

/**
 * JUnit tests for some core methods of cartesian geometric utilities class
 * 
 * @author gillespl
 *
 */
public class CartesianGeometricUtilitiesTest {

	public final double DOUBLE_THRESHOLD = .0001;

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void testCenterAndScale() {
		Tuple2D pair = new Tuple2D(1.5, .5);
		int width = 2;
		int height = 3;
		Tuple2D scaledPair = CartesianGeometricUtilities.centerAndScale(pair, width, height);
		assertTrue(scaledPair.x >= 0 && scaledPair.y <= 1);
		Tuple2D scaledUpper = CartesianGeometricUtilities.centerAndScale(new Tuple2D(2 - 1, 3 - 1), width, height);
		Tuple2D properUpper = new Tuple2D(1, 1);
		Tuple2D scaledLower = CartesianGeometricUtilities.centerAndScale(new Tuple2D(0, 0), width, height);
		Tuple2D properLower = new Tuple2D(-1, -1);
		assertEquals(scaledUpper.x, properUpper.x, DOUBLE_THRESHOLD);
		assertEquals(scaledUpper.y, properUpper.y, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.x, properLower.x, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.y, properLower.y, DOUBLE_THRESHOLD);

	}
	
	@Test
	public void testBottom1DCenterAndScale() {
		Tuple2D pair = new Tuple2D(1.5, .5);
		int width = 2;
		int height = 3;
		Tuple2D scaledPair = CartesianGeometricUtilities.Bottom1DCenterAndScale(pair, width, height);
		assertTrue(scaledPair.x == 0 && scaledPair.y <= 1);
		Tuple2D scaledUpper = CartesianGeometricUtilities.Bottom1DCenterAndScale(new Tuple2D(2 - 1, 3 - 1), width, height);
		Tuple2D properUpper = new Tuple2D(0, 1);
		Tuple2D scaledLower = CartesianGeometricUtilities.Bottom1DCenterAndScale(new Tuple2D(0, 0), width, height);
		Tuple2D properLower = new Tuple2D(0, 0);
		assertEquals(scaledUpper.x, properUpper.x, DOUBLE_THRESHOLD);
		assertEquals(scaledUpper.y, properUpper.y, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.x, properLower.x, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.y, properLower.y, DOUBLE_THRESHOLD);
	}
	
	@Test
	public void testBottomCenterAndScale() {
		Tuple2D pair = new Tuple2D(1.5, .5);
		int width = 2;
		int height = 3;
		Tuple2D scaledPair = CartesianGeometricUtilities.bottomCenterAndScale(pair, width, height);
		assertTrue(scaledPair.x >= -1 && scaledPair.y <= 1);
		Tuple2D scaledUpper = CartesianGeometricUtilities.bottomCenterAndScale(new Tuple2D(2 - 1, 3 - 1), width, height);
		Tuple2D properUpper = new Tuple2D(1, 1);
		Tuple2D scaledLower = CartesianGeometricUtilities.bottomCenterAndScale(new Tuple2D(0, 0), width, height);
		Tuple2D properLower = new Tuple2D(-1, 0);
		assertEquals(scaledUpper.x, properUpper.x, DOUBLE_THRESHOLD);
		assertEquals(scaledUpper.y, properUpper.y, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.x, properLower.x, DOUBLE_THRESHOLD);
		assertEquals(scaledLower.y, properLower.y, DOUBLE_THRESHOLD);
		
	}
	
	@Test
	public void testBottomScale() {
		double toScale = 8.0;
		assertEquals(4.0, CartesianGeometricUtilities.bottomScale(toScale, 3), DOUBLE_THRESHOLD);
		assertEquals(0, CartesianGeometricUtilities.bottomScale(0, 1), DOUBLE_THRESHOLD); //special case if max dimension is 1
		assertEquals(2.0, CartesianGeometricUtilities.bottomScale(toScale, 5), DOUBLE_THRESHOLD);
		assertEquals(1.0, CartesianGeometricUtilities.bottomScale(toScale, 9), DOUBLE_THRESHOLD);
	}

	@Test
	public void testShortestDistanceToLineSegment() {
		ILocated2D bottomOfSegment = new Tuple2D(0, 0);
		ILocated2D topOfSegment = new Tuple2D(0, 1);
		ILocated2D point = new Tuple2D(1, 0);
		ILocated2D otherPoint = new Tuple2D(1, 1.5);
		ILocated2D paramLessThanZero = new Tuple2D(0, -1);
		ILocated2D paramGreaterThanOne = new Tuple2D(1, 2);
		assertEquals(CartesianGeometricUtilities.shortestDistanceToLineSegment(point, topOfSegment, bottomOfSegment),
				1.0, DOUBLE_THRESHOLD);
		assertEquals(
				CartesianGeometricUtilities.shortestDistanceToLineSegment(otherPoint, topOfSegment, bottomOfSegment),
				1.1180, DOUBLE_THRESHOLD);
		assertEquals( // test for case if param < 0
				CartesianGeometricUtilities.shortestDistanceToLineSegment(paramLessThanZero, topOfSegment, bottomOfSegment),
				1.0, DOUBLE_THRESHOLD);
		assertEquals( // test for case if param > 1
				CartesianGeometricUtilities.shortestDistanceToLineSegment(paramGreaterThanOne, topOfSegment, bottomOfSegment),
				1.4142, DOUBLE_THRESHOLD);
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
		assertEquals(5, CartesianGeometricUtilities.euclideanDistance(x1, x2), DOUBLE_THRESHOLD);
	}

	@Test
	public void testPolarToCartesian() {
		double r = Math.sqrt(2);
		double theta = Math.PI / 4.0;
		double[] cart = CartesianGeometricUtilities.polarToCartesian(r, theta);
		double[] actual = { 1, 1 };
		for (int i = 0; i < cart.length; i++) {
			assertEquals(cart[i], actual[i], DOUBLE_THRESHOLD);
		}
	}

	@Test
	public void testSourceHeadingTowardsTarget() {
		ILocated2D source = new Tuple2D(.5, .5);
		ILocated2D target = new Tuple2D(1, 1);
		assertTrue(CartesianGeometricUtilities.sourceHeadingTowardsTarget(Math.PI / 4, source, target, Math.PI / 8));
		assertFalse(
				CartesianGeometricUtilities.sourceHeadingTowardsTarget(-Math.PI / 4, source, target, DOUBLE_THRESHOLD));
	}

	@Test
	public void testSignedAngleFromSourceHeadingToTarget() {
		ILocated2D source = new Tuple2D(1, 1);
		ILocated2D target1 = new Tuple2D(2, 1);
		ILocated2D target2 = new Tuple2D(1, 1);
		assertEquals(CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(source, target1, Math.PI / 4),
				-Math.PI / 4, DOUBLE_THRESHOLD);
		assertEquals(CartesianGeometricUtilities.signedAngleFromSourceHeadingToTarget(source, target2, Math.PI / 4),
				0, DOUBLE_THRESHOLD); //tests special case (if difference is 0)
	}

	@Test
	public void testOnRightSide() {
		ILocated2D source = new Tuple2D(.5, .5);
		ILocated2D target = new Tuple2D(1, 1);
		assertFalse(CartesianGeometricUtilities.onSideOf(source, Math.PI / 4, target, true));
		ILocated2D otherTarget = new Tuple2D(-1, -1);
		assertTrue(CartesianGeometricUtilities.onSideOf(source, Math.PI / 4, otherTarget, true));
	}

	@Test
	public void testSignedAngleDifference() {
		double rad1 = Math.PI / 4;
		double rad2 = Math.PI / 2;
		double rad3 = 4* Math.PI;
		double rad4 = 0;
		assertEquals(CartesianGeometricUtilities.signedAngleDifference(rad1, rad2), rad1, DOUBLE_THRESHOLD);
		assertEquals(CartesianGeometricUtilities.signedAngleDifference(rad2, rad1), -rad1, DOUBLE_THRESHOLD);
		assertEquals(CartesianGeometricUtilities.signedAngleDifference(rad1, rad1), 0, DOUBLE_THRESHOLD);
		assertEquals(CartesianGeometricUtilities.signedAngleDifference(rad3, rad4), -2*Math.PI, DOUBLE_THRESHOLD); //tests correction if exceeding 2pi
		assertEquals(CartesianGeometricUtilities.signedAngleDifference(rad4, rad3), 2*Math.PI, DOUBLE_THRESHOLD); //tests correction if going below 2pi
	}

	@Test
	public void testRestrictRadians() {
		double rads1 = 4 * Math.PI;
		double rads2 = 4 * Math.PI - .1;
		assertEquals(CartesianGeometricUtilities.restrictRadians(rads1), 0.0, DOUBLE_THRESHOLD);
		assertEquals(CartesianGeometricUtilities.restrictRadians(rads2), 2 * Math.PI - .1, DOUBLE_THRESHOLD);
	}

}
