package edu.utexas.cs.nn.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanUtilTest extends BooleanUtil {

	boolean[] test1 = new boolean[]{false, false, false}; // All false; both methods should return false
	boolean[] test2 = new boolean[]{true, true, false}; // One false; "all()" should return false, "any()" should return true
	boolean[] test3 = new boolean[]{true, false, false}; // One true; "all()" should return false, "any()" should return true
	boolean[] test4 = new boolean[]{true, true, true}; // All true; both methods should return true
	boolean[] test5 = new boolean[]{}; // Empty; "any()" returns false, "all()" returns true due to how the methods are implemented
	
	@Test
	public void testAny() {
		assertFalse(any(test1));
		assertTrue(any(test2));
		assertTrue(any(test3));
		assertTrue(any(test4));
		assertFalse(any(test5)); // Returns false for an empty Array
	}

	@Test
	public void testAll() {
		assertFalse(all(test1));
		assertFalse(all(test2));
		assertFalse(all(test3));
		assertTrue(all(test4));
		assertTrue(all(test5)); // Returns true for an empty Array
	}

}
