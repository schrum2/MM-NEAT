package edu.utexas.cs.nn.util.stats;

import static org.junit.Assert.*;
import org.junit.Test;

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
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }), 9);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 0), 9);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 1), 8);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 2), 7);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 3), 6);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 4), 5);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 5), 4);
		assertEquals(StatisticsUtilities.argmax(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 9), 0);
	}

	@Test
	public void testArgmaxIntArray() {// TODO
	}

	@Test
	public void testArgmaxIntArrayInt() {// TODO
	}

	@Test
	public void testArgminDoubleArray() {// TODO
	}

	@Test
	public void testArgminIntArray() {// TODO
	}

	@Test
	public void testSoftmax() {// TODO
	}

	@Test
	public void testProbabilistic() {// TODO
	}

	@Test
	public void testDistributionDoubleArray() {// TODO
	}

	@Test
	public void testDistributionIntArray() {// TODO
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
