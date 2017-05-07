package edu.utexas.cs.nn.util.stats;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;

public class StatisticsUtilitiesTest {

	private static final double ERROR = .0001;

	@Before
	public void setUp() throws Exception {
		MMNEAT.clearClasses();
		Parameters.initializeParameterCollections(new String[] {"randomArgMaxTieBreak:false", "io:false", "netio:false"});
	}
	
	@After
	public void tearDown() throws Exception {
		MMNEAT.clearClasses();
	}
	
	@Test
	public void testPercentile() {
		final double[] values = { 1, 2, 3, 4, 5 };
		final double p = 1.0;
		double answer = StatisticsUtilities.percentile(values, p);
		assertEquals(answer, 1.0, ERROR);
	}

	@Test
	public void testMedian() {
		double[] test1 = { 1, 2, 3 };
		double[] test2 = { 1, 50, 100 };
		double[] test3 = { 1, 1, 100, 50 };
		double[] test4 = { 1, 1, 1, 1 };
		assertEquals(StatisticsUtilities.median(test1), 2.0, ERROR);
		assertEquals(StatisticsUtilities.median(test2), 50.0, ERROR);
		assertEquals(StatisticsUtilities.median(test3), 51 / 2.0, ERROR);
		assertEquals(StatisticsUtilities.median(test4), 1.0, ERROR);
	}

	@Test
	public void testArgmaxDoubleArray() {
		double[] test1 = {4, 3, 2, 1};
		double[] test2 = {4, 5, 5};
		assertEquals(StatisticsUtilities.argmax(test1), 0);
		assertEquals(test1[StatisticsUtilities.argmax(test1)], 4, ERROR);
		assertEquals(StatisticsUtilities.argmax(test2), 1);
		assertEquals(test2[StatisticsUtilities.argmax(test2)], 5, ERROR);
	}

	@Test
	public void testArgmaxIntArray() {
		int[] test1 = {4, 3, 2, 1};
		int[] test2 = {4, 5, 5};
		assertEquals(StatisticsUtilities.argmax(test1), 0);
		assertEquals(test1[StatisticsUtilities.argmax(test1)], 4);
		//assertEquals(StatisticsUtilities.argmax(test2), 1);
		assertEquals(test2[StatisticsUtilities.argmax(test2)], 5);
	}

	@Test
	public void testArgmaxIntArrayInt() {
		int test[] = {5, 5, 4, 4, 3, 2, 1};
		assertEquals(StatisticsUtilities.argmax(test, 0), 0);
		assertEquals(test[StatisticsUtilities.argmax(test, 0)], 5);
		assertEquals(StatisticsUtilities.argmax(test, 1), 1);
		assertEquals(test[StatisticsUtilities.argmax(test, 1)], 5);
		assertEquals(StatisticsUtilities.argmax(test, 2), 2);
		assertEquals(test[StatisticsUtilities.argmax(test, 2)], 4);
		assertEquals(StatisticsUtilities.argmax(test, 3), 3);
		assertEquals(test[StatisticsUtilities.argmax(test, 3)], 4);
		assertEquals(StatisticsUtilities.argmax(test, 4), 4);
		assertEquals(test[StatisticsUtilities.argmax(test, 4)], 3);
		assertEquals(StatisticsUtilities.argmax(test, 5), 5);
		assertEquals(test[StatisticsUtilities.argmax(test, 5)], 2);
		assertEquals(StatisticsUtilities.argmax(test, 6), 6);
		assertEquals(test[StatisticsUtilities.argmax(test, 6)], 1);
	}

	@Test
	public void testArgminDoubleArray() {
		double[] test1 = {4, 3, 2, 1};
		double[] test2 = {4, 5, 5};
		assertEquals(StatisticsUtilities.argmin(test1), 3);
		assertEquals(test1[StatisticsUtilities.argmin(test1)], 1, ERROR);
		assertEquals(StatisticsUtilities.argmin(test2), 0);
		assertEquals(test2[StatisticsUtilities.argmin(test2)], 4, ERROR);
	}

	@Test
	public void testArgminIntArray() {
		int[] test1 = {4, 3, 2, 1};
		int[] test2 = {4, 5, 5};
		assertEquals(StatisticsUtilities.argmin(test1), 3);
		assertEquals(test1[StatisticsUtilities.argmin(test1)], 1);
		assertEquals(StatisticsUtilities.argmin(test2), 0);
		assertEquals(test2[StatisticsUtilities.argmin(test2)], 4);
	}

	@Test
	public void testSoftmax() {
		double[] test = {6};
		double temp = 3;
		double[] test1 = {2, 2};
		double temp1 = 2;
		assertEquals(StatisticsUtilities.softmax(test, temp), 0);
		int result1 = StatisticsUtilities.softmax(test1, temp1);
		assertFalse(result1 == 2);
		assertTrue(result1 == 0 || result1 == 1);
	}

	@Test
	public void testProbabilistic() {
		double[] probs1 = {1.0, -1.0, -1.0};
		double[] probs2 = {-1.0, 1.0, -1.0};
		double[] probs1or2 = {0.5, 0.5, -1.0};
		int shouldBe1 = StatisticsUtilities.probabilistic(probs1);
		assertEquals(shouldBe1, 0);
		int shouldBe2 = StatisticsUtilities.probabilistic(probs2);
		assertEquals(shouldBe2, 1);
		int shouldNotBe3 = StatisticsUtilities.probabilistic(probs1or2);
		assertTrue(shouldNotBe3 == 0 || shouldNotBe3 == 1);
		assertFalse(shouldNotBe3 == 2);
	}

	@Test
	public void testDistributionDoubleArray() {
		double[] test = {1, 2, 3};
		assertEquals(StatisticsUtilities.distribution(test)[0], (1.0 / 6.0), ERROR);
		assertEquals(StatisticsUtilities.distribution(test)[1], (2.0 / 6.0), ERROR);
		assertEquals(StatisticsUtilities.distribution(test)[2], (3.0 / 6.0), ERROR);
	}

	@Test
	public void testDistributionIntArray() {
		int[] test = {1, 2, 3};
		assertEquals(StatisticsUtilities.distribution(test)[0], (1.0 / 6.0), ERROR);
		assertEquals(StatisticsUtilities.distribution(test)[1], (2.0 / 6.0), ERROR);
		assertEquals(StatisticsUtilities.distribution(test)[2], (3.0 / 6.0), ERROR);
	}

	@Test
	public void testModeDoubleArray() {
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1, 2, 2, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8, 9 }), 8.0, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1 }), 1, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 2, 1, 2, 1, 2, 1 }), 2, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 2, 3, 1, 3, 2, 3, 1, 3, 2, 3, 1, 3 }), 3, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 20, 20.2, 1.0, 1.05, 1.05, 2, 4.5 }), 1.05, ERROR);
	}

	@Test
	public void testModeDoubleArrayDouble() {
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1 }, 0.001), 1, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1 }, 0.1), 1, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1.01, 1.01, 1.01 }, 0.1), 1.0, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1.01, 1.01, 1.01 }, 0.01), 1.01, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 20, 20.2, 1.0, 1.05, 1.05, 2, 4.5 }, 0.1), 1.0, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 20, 20.2, 1.0, 1.05, 1.05, 2, 4.5 }, 0.01), 1.05, ERROR);
		
	}

	@Test
	public void testMaximum() {
		double[] test = { 1, 1, 1, 12, 2, 2, 2, 23, 3, 3, 3, 500};
		double ans = StatisticsUtilities.maximum(test);
		double[] test2 = { 1, 2, 3, 4, 5 };
		double[] test5 = { 1, 50, 100 };
		double[] test3 = { 1, 1, 100, 50 };
		double[] test4 = { 1, 1, 1, 1 };
		assertEquals(ans, 500, ERROR);
		assertEquals(StatisticsUtilities.maximum(test2), 5, ERROR);
		assertEquals(StatisticsUtilities.maximum(test5), 100, ERROR);
		assertEquals(StatisticsUtilities.maximum(test3), 100, ERROR);
		assertEquals(StatisticsUtilities.maximum(test4), 1, ERROR);
	}

	@Test
	public void testMaximumCollectionOfInteger() {
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		test.add(5);
		assertEquals(StatisticsUtilities.maximum(test), 5);
		ArrayList<Integer> test2 = new ArrayList<Integer>();
		test2.add(1);
		test2.add(50);
		test2.add(100);
		assertEquals(StatisticsUtilities.maximum(test2), 100, ERROR);
		test2.add(150);
		assertEquals(StatisticsUtilities.maximum(test2), 150, ERROR);
	}

	@Test
	public void testMaximumLongArray() {
		long[] test1 = { 1, 2, 3, 4, 5 };
		long[] test2 = { 1, Integer.MAX_VALUE, ((long) Integer.MAX_VALUE) + 1 };
		assertEquals(StatisticsUtilities.maximum(test1), 5, ERROR);
		assertEquals(StatisticsUtilities.maximum(test2), ((long) Integer.MAX_VALUE) + 1 , ERROR);
	}

	@Test
	public void testMinimum() {
		double[] test = { 1, 1, 1, 12, 2, 2, 2, 23, 3, 3, 3, 500};
		double[] test2 = { 1, 2, 3, 4, 5 };
		double[] test3 = { 1, 1, 100, 50 };
		double[] test4 = { 50, 50, 51, 51 };
		double[] test5 = { 100, 500, 101 };
		assertEquals(StatisticsUtilities.minimum(test), 1, ERROR);
		assertEquals(StatisticsUtilities.minimum(test2), 1, ERROR);
		assertEquals(StatisticsUtilities.minimum(test3), 1, ERROR);
		assertEquals(StatisticsUtilities.minimum(test4), 50, ERROR);
		assertEquals(StatisticsUtilities.minimum(test5), 100, ERROR);
	}

	@Test
	public void testAverage() {
		double[] test = {5, 9};
		assertEquals(StatisticsUtilities.average(test), 7.0, ERROR);
		double[] test1 = {5, 5, 5, 5, 5, 5, 5, 5};
		assertEquals(StatisticsUtilities.average(test1), 5.0, ERROR);
		double[] test2 = {0, 0};
		assertEquals(StatisticsUtilities.average(test2), 0.0, ERROR);
		double[] test3 = {0, 1};
		assertEquals(StatisticsUtilities.average(test3), 0.5, ERROR);
		double[] test4 = {0.1, 1.5, 3.5, 2.5};
		assertEquals(StatisticsUtilities.average(test4), 1.9, ERROR);
	}

	@Test
	public void testSumIntArray() {
		int[] test = {1, 2, 3, 4, 5};
		assertEquals(StatisticsUtilities.sum(test), 15);
		int[] test1 = {50, 50, 50};
		assertEquals(StatisticsUtilities.sum(test1), 150);
		int[] test2 = {50, -50};
		assertEquals(StatisticsUtilities.sum(test2), 0);
	}

	@Test
	public void testSumDoubleArray() {
		double[] test = {1, 2, 3, 4, 5};
		assertEquals(StatisticsUtilities.sum(test), 15, ERROR);
		double[] test1 = {50, 50, 50};
		assertEquals(StatisticsUtilities.sum(test1), 150, ERROR);
		double[] test2 = {50, -50};
		assertEquals(StatisticsUtilities.sum(test2), 0, ERROR);
		double[] test3 = {0.25, 0.75};
		assertEquals(StatisticsUtilities.sum(test3), 1, ERROR);
		double[] test4 = {1, -0.5};
		assertEquals(StatisticsUtilities.sum(test4), .5, ERROR);
	}

	@Test
	public void testSumCollectionOfInteger() {
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(2);
		test.add(3);
		test.add(4);
		test.add(5);
		assertEquals(StatisticsUtilities.sum(test), 15);
		ArrayList<Integer> test2 = new ArrayList<Integer>();
		test2.add(1);
		test2.add(5);
		assertEquals(StatisticsUtilities.sum(test2), 6);
		test2.add(-5);
		assertEquals(StatisticsUtilities.sum(test2), 1);
	}

	@Test
	public void testPopulationStandardDeviation() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {0, 10, 50};
		double[] test3 = {2, 4, 6, 8};
		assertEquals(StatisticsUtilities.populationStandardDeviation(test1), Math.sqrt(2.0 / 3.0), ERROR);
		assertEquals(StatisticsUtilities.populationStandardDeviation(test2), Math.sqrt(1400.0 / 3.0), ERROR);
		assertEquals(StatisticsUtilities.populationStandardDeviation(test3), Math.sqrt(5.0), ERROR);
	}

	@Test
	public void testSampleStandardDeviation() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {0, 10, 50};
		double[] test3 = {2, 4, 6, 8};
		assertEquals(StatisticsUtilities.sampleStandardDeviation(test1), Math.sqrt(1), ERROR);
		assertEquals(StatisticsUtilities.sampleStandardDeviation(test2), Math.sqrt(700), ERROR);
		assertEquals(StatisticsUtilities.sampleStandardDeviation(test3), Math.sqrt(20.0 / 3.0), ERROR);
	}

	@Test
	public void testPopulationVariance() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {0, 10, 50};
		double[] test3 = {2, 4, 6, 8};
		assertEquals(StatisticsUtilities.populationVariance(test1), 2.0 / 3.0, ERROR);
		assertEquals(StatisticsUtilities.populationVariance(test2), 1400.0 / 3.0, ERROR);
		assertEquals(StatisticsUtilities.populationVariance(test3), 5.0, ERROR);
	}

	@Test
	public void testSampleVariance() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {0, 10, 50};
		double[] test3 = {2, 4, 6, 8};
		assertEquals(StatisticsUtilities.sampleVariance(test1), 1, ERROR);
		assertEquals(StatisticsUtilities.sampleVariance(test2), 700, ERROR);
		assertEquals(StatisticsUtilities.sampleVariance(test3), 20.0 / 3.0, ERROR);
	}

	@Test
	public void testSumOfSquares() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {0, 10, 50};
		double[] test3 = {2, 4, 6, 8};
		assertEquals(StatisticsUtilities.sumOfSquares(test1), 2, ERROR);
		assertEquals(StatisticsUtilities.sumOfSquares(test2), 1400, ERROR);
		assertEquals(StatisticsUtilities.sumOfSquares(test3), 20, ERROR);
	}

	@Test
	public void testTValue() {
		assertEquals(StatisticsUtilities.tValue(29), 2.048, ERROR);
		assertEquals(StatisticsUtilities.tValue(24), 2.069, ERROR);
		assertEquals(StatisticsUtilities.tValue(2), 12.706, ERROR);
		assertEquals(StatisticsUtilities.tValue(13), 2.179, ERROR);
		assertEquals(StatisticsUtilities.tValue(7), 2.447, ERROR);
	}
}
