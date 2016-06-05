package edu.utexas.cs.nn.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MiscUtilTest {

	@Test
	public void testMultiplicationFactor() {
		assertEquals(1, MiscUtil.multiplicationFactor(4, 3));
		assertEquals(2, MiscUtil.multiplicationFactor(4, 5));
		assertEquals(2, MiscUtil.multiplicationFactor(4, 7));
		assertEquals(4, MiscUtil.multiplicationFactor(4, 15));
		assertEquals(4, MiscUtil.multiplicationFactor(4, 16));
	}

}
