package edu.utexas.cs.nn.util.util2D;

import static org.junit.Assert.*;

import org.junit.Test;

public class Box2DTest {

	ILocated2D[] testBox1 = new ILocated2D[]{new Tuple2D(50.0, 0.0), new Tuple2D(-50.0, 0.0), new Tuple2D(0.0, 50.0), new Tuple2D(0.0, -50.0)};

	Box2D testBox2D1 = new Box2D(testBox1);
	
	ILocated2D testPoint1 = new Tuple2D(0.0, 0.0);
	ILocated2D testPoint2 = new Tuple2D(42.0, -36.5);
	ILocated2D testPoint3 = new Tuple2D(-13.4, 98.8);
	ILocated2D testPoint4 = new Tuple2D(-24.1, -62.6);
	ILocated2D testPoint5 = new Tuple2D(4.2, 7.3);
	ILocated2D testPoint6 = new Tuple2D(51.0, 52.0);
	ILocated2D testPoint7 = new Tuple2D(-50.1, -54.0);
	
	
	
	@Test
	public void testInsideBoxILocated2D() {
		assertTrue(testBox2D1.insideBox(testPoint1));
		assertTrue(testBox2D1.insideBox(testPoint2));
		assertFalse(testBox2D1.insideBox(testPoint3));
		assertFalse(testBox2D1.insideBox(testPoint4));
		assertTrue(testBox2D1.insideBox(testPoint5));
		assertFalse(testBox2D1.insideBox(testPoint6));
		assertFalse(testBox2D1.insideBox(testPoint7));
	}

	@Test
	public void testInsideBoxILocated2DDouble() {
		assertTrue(testBox2D1.insideBox(testPoint1, 3.0));
		assertTrue(testBox2D1.insideBox(testPoint2, 3.0));
		assertFalse(testBox2D1.insideBox(testPoint3, 3.0));
		assertFalse(testBox2D1.insideBox(testPoint4, 3.0));
		assertTrue(testBox2D1.insideBox(testPoint5, 3.0));
		assertTrue(testBox2D1.insideBox(testPoint6, 3.0));
		assertFalse(testBox2D1.insideBox(testPoint7, 3.0));		
	}

}
