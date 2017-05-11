package edu.utexas.cs.nn.util.util2D;

import static org.junit.Assert.*;

import org.junit.Test;

public class Box2DTest {

	ILocated2D[] testBox1 = new ILocated2D[]{new Tuple2D(50.0, 0.0), new Tuple2D(-50.0, 0.0), new Tuple2D(0.0, 50.0), new Tuple2D(0.0, -50.0)};
	ILocated2D[] testBox2 = new ILocated2D[]{new Tuple2D(100.0, 75.0), new Tuple2D(0.0, 0.0), new Tuple2D(-60.0, 50.0), new Tuple2D(42.0, -31.0)};

	Box2D testBox2D1 = new Box2D(testBox1); // Each point in testBox1 determines a single dimension of 2DBox1
	Box2D testBox2D2 = new Box2D(testBox2); // One point in testBox2 determines two dimensions, two points determine a single dimension each, and one point doesn't determine any dimensions
	
	ILocated2D testPoint1 = new Tuple2D(0.0, 0.0); // In both boxes
	ILocated2D testPoint2 = new Tuple2D(50.0, -50.0); // In neither Box, but in Box1 with buffer of 5
	ILocated2D testPoint3 = new Tuple2D(-30.0, 90.0); // In neither Box
	ILocated2D testPoint4 = new Tuple2D(50.0, -30.0); // In Box1, not Box2
	ILocated2D testPoint5 = new Tuple2D(4.2, 7.3); // In both Boxes
	ILocated2D testPoint6 = new Tuple2D(51.0, 52.0); // In Box2, but in Box1 with buffer of 5
	ILocated2D testPoint7 = new Tuple2D(-50.1, -54.0); // in neither box, but in Box1 with buffer of 5
	
	
	
	@Test
	public void testInsideBoxILocated2D() {
		
		// Testing which of these points are in Box1
		assertTrue(testBox2D1.insideBox(testPoint1));
		assertFalse(testBox2D1.insideBox(testPoint2));
		assertFalse(testBox2D1.insideBox(testPoint3));
		assertFalse(testBox2D1.insideBox(testPoint4));
		assertTrue(testBox2D1.insideBox(testPoint5));
		assertFalse(testBox2D1.insideBox(testPoint6));
		assertFalse(testBox2D1.insideBox(testPoint7));

		// Testing which of these points are in Box2
		assertTrue(testBox2D2.insideBox(testPoint1));
		assertFalse(testBox2D2.insideBox(testPoint2));
		assertFalse(testBox2D2.insideBox(testPoint3));
		assertTrue(testBox2D2.insideBox(testPoint4));
		assertTrue(testBox2D2.insideBox(testPoint5));
		assertTrue(testBox2D2.insideBox(testPoint6));
		assertFalse(testBox2D2.insideBox(testPoint7));
	}

	@Test
	public void testInsideBoxILocated2DDouble() {
		
		// Testing how a positive buffer functions; should include more points
		assertTrue(testBox2D1.insideBox(testPoint1, 5.0));
		assertTrue(testBox2D1.insideBox(testPoint2, 5.0)); // Now in Box1 due to buffer
		assertFalse(testBox2D1.insideBox(testPoint3, 5.0));
		assertTrue(testBox2D1.insideBox(testPoint4, 5.0));
		assertTrue(testBox2D1.insideBox(testPoint5, 5.0));
		assertTrue(testBox2D1.insideBox(testPoint6, 5.0)); // Now in Box1 due to buffer
		assertTrue(testBox2D1.insideBox(testPoint7, 5.0)); // Now in Box1 due to buffer
		
		// Testing how a negative buffer functions; should exclude more points
		assertTrue(testBox2D2.insideBox(testPoint1, -10.0));
		assertFalse(testBox2D2.insideBox(testPoint2, -10.0));
		assertFalse(testBox2D2.insideBox(testPoint3, -10.0));
		assertFalse(testBox2D2.insideBox(testPoint4, -10.0)); // Now out of Box2 due to buffer
		assertTrue(testBox2D2.insideBox(testPoint5, -10.0));
		assertTrue(testBox2D2.insideBox(testPoint6, -10.0));
		assertFalse(testBox2D2.insideBox(testPoint7, -10.0));
	}

}
