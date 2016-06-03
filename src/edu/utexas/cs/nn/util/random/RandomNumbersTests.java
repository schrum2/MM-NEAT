package edu.utexas.cs.nn.util.random;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

import edu.utexas.cs.nn.parameters.Parameters;

public class RandomNumbersTests {

	/**
	 * Tests that the current randomGenerator is replaced by a new one when reset
	 */
	@Test
	public void reset_test() {
		Random rand1 = RandomNumbers.randomGenerator;
		Parameters.initializeParameterCollections( new String[] { "randomSeed:-1" });
		RandomNumbers.reset();
		Random rand2 = RandomNumbers.randomGenerator;
		assertFalse(rand1 == rand2);
	}

	/**
	 * Tests that the current randomGenerator is replaced by a new one when reset
	 */
	@Test
	public void reset_seed_test() {
		Random rand1 = RandomNumbers.randomGenerator;
		Parameters.initializeParameterCollections( new String[] { "randomSeed:2" });
		RandomNumbers.reset();
		Random rand2 = RandomNumbers.randomGenerator;
		assertFalse(rand1 == rand2);
	}
	
	/**
	 * Tests that given results are in range [-1,1]
	 */
	@Test
	public void fullSmallRand_test() {
		double test1 = RandomNumbers.fullSmallRand();
		assertTrue(test1 < 1);
		assertTrue(test1 > -1);
		double test2 = RandomNumbers.fullSmallRand();
		assertTrue(test2 < 1);
		assertTrue(test2 > -1);
		double test3 = RandomNumbers.fullSmallRand();
		assertTrue(test3 < 1);
		assertTrue(test3 > -1);
	}
	
	@Test
	public void boundedRandom_test() {
		double test1 = RandomNumbers.boundedRandom(-1.0, 1.0);
		assertTrue(test1 < 1);
		assertTrue(test1 > -1);
		double test2 = RandomNumbers.boundedRandom(2.0, 5.0);
		assertTrue(test2 < 5);
		assertTrue(test2 > 2);
		double test3 = RandomNumbers.boundedRandom(6, 90);
		assertTrue(test3 < 90);
		assertTrue(test3 > 6);
	}
	
	@Test
	public void randomArray_test() {
		double[] test = RandomNumbers.randomArray(5);
		assertTrue(test[0] < 1);
		assertTrue(test[0] > -1);
		assertTrue(test[1] < 1);
		assertTrue(test[1] > -1);
		assertTrue(test[2] < 1);
		assertTrue(test[2] > -1);
		assertTrue(test[3] < 1);
		assertTrue(test[3] > -1);
		assertTrue(test[4] < 1);
		assertTrue(test[4] > -1);
	}

	@Test
	public void randomBoundedArray_test() {
		double[] lower = {2, 4, 6, 8};
		double[] upper = {3, 6, 9, 12};
		double[] result = RandomNumbers.randomBoundedArray(lower, upper);
		assertTrue(result[0] < 3);
		assertTrue(result[0] > 2);
		assertTrue(result[1] < 6);
		assertTrue(result[1] > 2);
		assertTrue(result[2] < 9);
		assertTrue(result[2] > 6);
		assertTrue(result[3] < 12);
		assertTrue(result[3] > 8);
	}
	
	@Test
	public void randomIntArray_test() {
		
	}
	
	@Test
	public void probabilisticSelection_test() {
		
	}
	
	@Test
	public void randomDistinct_test() {
		
	}
	
	@Test
	public void randomSign_test() {
		
	}
	
	@Test
	public void randomXofY_test() {
		
	}
	
	@Test
	public void randomCauchyValue_test() {
		
	}
	
	@Test
	public void randomCauchyValue_wtrange_test() {
		
	}
	
	@Test
	public void randomElement_test() {
		
	}
}
