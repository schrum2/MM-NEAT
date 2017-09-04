package edu.southwestern.util.util2D;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Distance2DComparatorTest {

	Tuple2D bottomLeft;
	Tuple2D topLeft;
	Tuple2D bottomRight;
	Tuple2D topRight;
	 
	Tuple2D topCenter;
	Tuple2D farBottomLeft;
	

	@Before
	public void setUp() throws Exception {
		bottomLeft = new Tuple2D(1.0, 1.0);
		topLeft = new Tuple2D(1.0, 8.0);
		bottomRight = new Tuple2D(8.0, 1.0);
		topRight = new Tuple2D(8.0, 8.0);
		
		topCenter = new Tuple2D(4.5, 8.0);
		farBottomLeft = new Tuple2D(-200, -200);
	}


	@Test
	public void testDistance2DComparator() {
		Distance2DComparator testComparator = new Distance2DComparator(topRight);
		assertNotNull(testComparator);
	}

	@Test
	public void testCompare() {
		Distance2DComparator vsBottomLeft = new Distance2DComparator(bottomLeft);
		//same distance
		assertEquals(0, vsBottomLeft.compare(topLeft, bottomRight)); 
		//topRight is farthest among the corners
		assertEquals(1, vsBottomLeft.compare(topRight, bottomRight)); 
		assertEquals(-1, vsBottomLeft.compare(topLeft, topRight));
		//non corner
		assertEquals(1, vsBottomLeft.compare(topCenter, bottomRight));
		assertEquals(1, vsBottomLeft.compare(topCenter, bottomLeft));
		assertEquals(-1, vsBottomLeft.compare(topCenter, topRight));
		
		//large and negative valued point
		Distance2DComparator vsFar = new Distance2DComparator(farBottomLeft); // this is never used
		assertEquals(1, vsFar.compare(topCenter, bottomRight));
		assertEquals(-1, vsFar.compare(bottomLeft, bottomRight));
		assertEquals(0, vsFar.compare(topLeft, bottomRight)); 
	}

}
