package edu.utexas.cs.nn.util.util2D;

import static org.junit.Assert.*;

import org.junit.Test;

public class Tuple2DTest {

	public static final double ERROR = .0001;

	@Test
	public void testTuple2D() {
		Tuple2D test = new Tuple2D(1, 2);
		assertEquals(test.x, 1, ERROR);
		assertEquals(test.y, 2, ERROR);
	}

	@Test
	public void testIsZero() {
		Tuple2D test = new Tuple2D(1, 2);
		Tuple2D otherTest = new Tuple2D(0, 0);
		assertFalse(test.isZero());
		assertTrue(otherTest.isZero());
	}

	@Test
	public void testLength() {
		Tuple2D test = new Tuple2D(3, 4);
		assertEquals(test.length(), 5, ERROR);
	}

	@Test
	public void testNormalize() {
		Tuple2D test = new Tuple2D(3, 4);
		double testX = 3.0 / 5;
		double testY = 4.0 / 5;
		test = test.normalize();
		assertEquals(testX, test.x, ERROR);
		assertEquals(testY, test.y, ERROR);
	}

	@Test
	public void testAngle() {
		Tuple2D test = new Tuple2D(1, 1);
		assertEquals(test.angle(), Math.PI / 4, ERROR);
	}

	@Test
	public void testMidpoint() {
		Tuple2D test = new Tuple2D(1, 1);
		Tuple2D zero = new Tuple2D(0, 0);
		test = test.midpoint(zero);
		Tuple2D otherTest = new Tuple2D(.5, .5);
		assertEquals(test.x, otherTest.x, ERROR);
		assertEquals(test.y, otherTest.y, ERROR);
		assertTrue(test.midpoint(test).equals(otherTest));
	}

	@Test
	public void testAdd() {
		Tuple2D test = new Tuple2D(2, 2);
		Tuple2D something = new Tuple2D(1, 1);
		test.add(something);
		Tuple2D otherTest = new Tuple2D(2, 2);
		assertEquals(test.x, otherTest.x, ERROR);
		assertEquals(test.y, otherTest.y, ERROR);
		assertTrue(test.equals(otherTest));
	}

	@Test
	public void testSub() {
		Tuple2D test = new Tuple2D(1, 1);
		test = test.sub(test);
		assertTrue(test.isZero());
	}

	@Test
	public void testMult() {
		Tuple2D test = new Tuple2D(2, 3);
		test = test.mult(2);
		assertEquals(test.x, 4, ERROR);
		assertEquals(test.y, 6, ERROR);
	}

	@Test
	public void testDiv() {
		Tuple2D test = new Tuple2D(4, 2);
		test = test.div(2);
		assertEquals(test.x, 2, ERROR);
		assertEquals(test.y, 1, ERROR);
	}

	@Test
	public void testGetPosition() {
		Tuple2D test = new Tuple2D(1, 1);
		Tuple2D otherTest = test.getPosition();
		assertTrue(test.equals(otherTest));
	}

	@Test
	public void testEquals() {
		Tuple2D test = new Tuple2D(1, 1);
		Tuple2D otherTest = new Tuple2D(1, 1);
		assertTrue(test.equals(otherTest));
	}

	@Test
	public void testRotate() {
		Tuple2D test = new Tuple2D(1, 0);
		Tuple2D otherTest = new Tuple2D(1, 0);
		test.rotate(Math.PI / 2);
		assertEquals(test, otherTest);
	}

	@Test
	public void testDistanceILocated2D() {
		Tuple2D test = new Tuple2D(1, 1);
		ILocated2D otherTest = new Tuple2D(0, 0);
		assertEquals(test.distance(otherTest), Math.sqrt(2), ERROR);
	}

	@Test
	public void testToString() {
		Tuple2D test = new Tuple2D(1, 1);
		String tostring = test.toString();
		String copy = "(1.0,1.0)";
		assertEquals(tostring, copy);
	}

	@Test
	public void testAngleBetweenTargets() {
		Tuple2D test = new Tuple2D(0, 0);
		Tuple2D a = new Tuple2D(1, 1);
		Tuple2D b = new Tuple2D(1, 0);
		double angle = test.angleBetweenTargets(a, b);
		assertEquals(angle, Math.PI / 4, ERROR);
	}

}
