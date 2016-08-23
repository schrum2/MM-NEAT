package edu.utexas.cs.nn.util.datastructures;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;

public class IntervalTest {

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}

	@Test
	public void testInterval() {
		Interval<Integer> i = new Interval<Integer>(true, 5, 20, true);
		assertEquals(i.toString(), "[5,20]");
		assertFalse(i.contains(-10));
		assertTrue(i.contains(5));
		assertTrue(i.contains(7));
		assertTrue(i.contains(20));
		assertFalse(i.contains(21));

		i = new Interval<Integer>(false, 5, 20, true);
		assertEquals(i.toString(), "]5,20]");
		assertFalse(i.contains(-10));
		assertFalse(i.contains(5));
		assertTrue(i.contains(7));
		assertTrue(i.contains(20));
		assertFalse(i.contains(21));

		i = new Interval<Integer>(false, 5, 20, false);
		assertEquals(i.toString(), "]5,20[");
		assertFalse(i.contains(-10));
		assertFalse(i.contains(5));
		assertTrue(i.contains(7));
		assertFalse(i.contains(20));
		assertFalse(i.contains(21));

		i = new Interval<Integer>(true, 5, 20, false);
		assertEquals(i.toString(), "[5,20[");
		assertFalse(i.contains(-10));
		assertTrue(i.contains(5));
		assertTrue(i.contains(7));
		assertFalse(i.contains(20));
		assertFalse(i.contains(21));
	}

}
