package edu.southwestern.util.random;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.After;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;

public class RandomNumbersTests {

	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	/**
	 * Tests that the current randomGenerator is replaced by a new one when reset
	 */
	@Test
	public void reset_test() {
		Random rand1 = RandomNumbers.randomGenerator;
		Parameters.initializeParameterCollections( new String[] { "randomSeed:-1", "io:false", "netio:false" });
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
		Parameters.initializeParameterCollections( new String[] { "randomSeed:2", "io:false", "netio:false" });
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
		int[] ceilings = {1, 3, 6, 9, 12};
		Integer[] result = RandomNumbers.randomIntArray(ceilings);
		assertEquals(result[0], (Integer)0);
		assertTrue(result[1] < 3);
		assertTrue(result[2] < 6);
		assertTrue(result[3] < 9);
		assertTrue(result[4] < 12);
		assertEquals(result.length, ceilings.length);

		Integer[] result2 = RandomNumbers.randomIntArray(new int[] {1});
		assertEquals(result2[0], (Integer)0);
		assertEquals(result2.length, 1);
	}
	
	@Test
	public void probabilisticSelection_test() {
		double[] probs1 = {1.0, 0.0, 0.0};
		double[] probs2 = {0.0, 1.0, 0.0};
		double[] probs1or2 = {0.5, 0.5, 0.0};
		int shouldBe1 = RandomNumbers.probabilisticSelection(probs1);
		assertEquals(shouldBe1, 0);
		int shouldBe2 = RandomNumbers.probabilisticSelection(probs2);
		assertEquals(shouldBe2, 1);
		int shouldNotBe3 = RandomNumbers.probabilisticSelection(probs1or2);
		assertTrue(shouldNotBe3 == 0 || shouldNotBe3 == 1);
		assertFalse(shouldNotBe3 == 2);
	}
	
	@Test
	public void randomDistinct_test() {
		int[] result = RandomNumbers.randomDistinct(4, 4); //(0, 1, 2, 3)
		assertEquals(result.length, 4);
		assertEquals(ArrayUtil.countOccurrences(0, result), 1);
		assertEquals(ArrayUtil.countOccurrences(1, result), 1);
		assertEquals(ArrayUtil.countOccurrences(2, result), 1);
		assertEquals(ArrayUtil.countOccurrences(3, result), 1);
	}
	
	@Test
	public void randomSign_test() {
		for(int i = 0; i < 50; i++){
			assertEquals(Math.abs(RandomNumbers.randomSign()), 1, 0.0);
		}
	}
	
	@Test
	public void randomXofY_test() {
		int[] result = RandomNumbers.randomXofY(4, 4); //(0, 1, 2, 3)
		assertEquals(result.length, 4);
		assertEquals(ArrayUtil.countOccurrences(0, result), 1);
		assertEquals(ArrayUtil.countOccurrences(1, result), 1);
		assertEquals(ArrayUtil.countOccurrences(2, result), 1);
		assertEquals(ArrayUtil.countOccurrences(3, result), 1);
	}
	
	@Test
	public void randomElement_test() {
		ArrayList<String> strings = new ArrayList<String>(2);
		strings.add("Yes");
		strings.add("No");
		String result = RandomNumbers.randomElement(strings);
		assertTrue(result.equals("Yes") || result.equals("No"));
		assertFalse(result.equals("Maybe"));
	}
}
