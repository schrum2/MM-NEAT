package edu.utexas.cs.nn.util.stats;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class StatisticsUtilitiesTest {

	private static final double ERROR = .0001;

	@Before
	public void setUp() throws Exception {
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
	}

	@Test
	public void testModeDoubleArrayDouble() {// TODO
	}

	@Test
	public void testMaximum() {
		double[] test = { 1, 1, 1, 12, 2, 2, 2, 23, 3, 3, 3, 500 };
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

}
