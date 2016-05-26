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
		final double[] values = {1, 2, 3, 4, 5};
		final double p = 1.0;
		double answer = StatisticsUtilities.percentile(values, p);
		assertEquals(answer, 1.0, ERROR);
	}

	@Test
	public void testMedian() {
		double[] test1 = {1, 2, 3};
		double[] test2 = {1, 50, 100};
		double[] test3 = {1,1, 100,50};
		double[] test4 = {1, 1, 1, 1};
		assertEquals(StatisticsUtilities.median(test1), 2.0, ERROR);
		assertEquals(StatisticsUtilities.median(test2), 50.0, ERROR);
		assertEquals(StatisticsUtilities.median(test3), 51/2.0, ERROR);
		assertEquals(StatisticsUtilities.median(test4), 1.0, ERROR);
	}

	@Test
	public void testArgmaxDoubleArray() {
		
	}

	@Test
	public void testArgmaxIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testArgmaxIntArrayInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testArgminDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testArgminIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSoftmax() {
		fail("Not yet implemented");
	}

	@Test
	public void testProbabilistic() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistributionDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testDistributionIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testModeDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testModeDoubleArrayDouble() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaximum() {
		double[] test = {1,1,1,12,2,2,2,23,3,3,3,500};
		double ans = StatisticsUtilities.maximum(test);
		double[] test2 = {1,2,3,4,5};
		double[] test5 = {1, 50, 100};
		double[] test3 = {1,1, 100,50};
		double[] test4 = {1, 1, 1, 1};
		assertEquals(ans, 500, ERROR);
		assertEquals(StatisticsUtilities.maximum(test2), 5, ERROR);
		assertEquals(StatisticsUtilities.maximum(test5), 100, ERROR);
		assertEquals(StatisticsUtilities.maximum(test3), 100, ERROR);
		assertEquals(StatisticsUtilities.maximum(test4), 1, ERROR);
	}

	@Test
	public void testMaximumCollectionOfInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testMaximumLongArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testMinimum() {
		fail("Not yet implemented");
	}

	@Test
	public void testAverage() {
		fail("Not yet implemented");
	}

	@Test
	public void testSumIntArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSumDoubleArray() {
		fail("Not yet implemented");
	}

	@Test
	public void testSumCollectionOfInteger() {
		fail("Not yet implemented");
	}

	@Test
	public void testPopulationStandardDeviation() {
		fail("Not yet implemented");
	}

	@Test
	public void testSampleStandardDeviation() {
		fail("Not yet implemented");
	}

	@Test
	public void testPopulationVariance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSampleVariance() {
		fail("Not yet implemented");
	}

	@Test
	public void testSumOfSquares() {
		fail("Not yet implemented");
	}

	@Test
	public void testInstantaneousErrorEnergy() {
		fail("Not yet implemented");
	}

	@Test
	public void testInstantaneousTotalErrorEnergy() {
		fail("Not yet implemented");
	}

	@Test
	public void testAverageSquaredErrorEnergy() {
		fail("Not yet implemented");
	}

}
