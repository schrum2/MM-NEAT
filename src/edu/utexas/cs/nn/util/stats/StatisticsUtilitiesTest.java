package edu.utexas.cs.nn.util.stats;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.utexas.cs.nn.util.random.RandomNumbers;

public class StatisticsUtilitiesTest {

	private static final double ERROR = .0001;

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
		assertEquals(test1[StatisticsUtilities.argmax(test1)], 4, 0.0);
		assertEquals(StatisticsUtilities.argmax(test2), 1);
		assertEquals(test2[StatisticsUtilities.argmax(test2)], 5, 0.0);
	}

	@Test
	public void testArgmaxIntArray() {
		int[] test1 = {4, 3, 2, 1};
		int[] test2 = {4, 5, 5};
		assertEquals(StatisticsUtilities.argmax(test1), 0);
		assertEquals(test1[StatisticsUtilities.argmax(test1)], 4);
		assertEquals(StatisticsUtilities.argmax(test2), 1);
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
		assertEquals(test1[StatisticsUtilities.argmin(test1)], 1, 0.0);
		assertEquals(StatisticsUtilities.argmin(test2), 0);
		assertEquals(test2[StatisticsUtilities.argmin(test2)], 4, 0.0);
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
	public void testSoftmax() {// TODO, don't know what this method does
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
		assertEquals(StatisticsUtilities.distribution(test)[0], (1.0 / 6.0), 0.0);
		assertEquals(StatisticsUtilities.distribution(test)[1], (2.0 / 6.0), 0.0);
		assertEquals(StatisticsUtilities.distribution(test)[2], (3.0 / 6.0), 0.0);
	}

	@Test
	public void testDistributionIntArray() {
		int[] test = {1, 2, 3};
		assertEquals(StatisticsUtilities.distribution(test)[0], (1.0 / 6.0), 0.0);
		assertEquals(StatisticsUtilities.distribution(test)[1], (2.0 / 6.0), 0.0);
		assertEquals(StatisticsUtilities.distribution(test)[2], (3.0 / 6.0), 0.0);
	}

	@Test
	public void testModeDoubleArray() {// TODO
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1, 2, 2, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8, 9 }), 8.0, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 1, 1, 1 }), 1, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 2, 1, 2, 1, 2, 1 }), 2, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 2, 3, 1, 3, 2, 3, 1, 3, 2, 3, 1, 3 }), 3, ERROR);
		assertEquals(StatisticsUtilities.mode(new double[] { 20, 20.2, 1.0, 1.05, 1.05, 2, 4.5 }), 1.05, ERROR);
	}

	@Test
	public void testModeDoubleArrayDouble() {// TODO
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
	public void testMaximumCollectionOfInteger() {// TODO
	}

	@Test
	public void testMaximumLongArray() {// TODO
	}

	@Test
	public void testMinimum() {// TODO
	}

	@Test
	public void testAverage() {// TODO
	}

	@Test
	public void testSumIntArray() {// TODO
	}

	@Test
	public void testSumDoubleArray() {// TODO
	}

	@Test
	public void testSumCollectionOfInteger() {// TODO
	}

	@Test
	public void testPopulationStandardDeviation() {// TODO
	}

	@Test
	public void testSampleStandardDeviation() {// TODO
	}

	@Test
	public void testPopulationVariance() {// TODO
	}

	@Test
	public void testSampleVariance() {// TODO
	}

	@Test
	public void testSumOfSquares() {// TODO
	}

	@Test
	public void testInstantaneousErrorEnergy() {// TODO
	}

	@Test
	public void testInstantaneousTotalErrorEnergy() {// TODO
	}

	@Test
	public void testAverageSquaredErrorEnergy() {// TODO
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
