package edu.southwestern.util.stats;

/**
 * Compute the average of an array of doubles
 * 
 * @author Jacob Schrum
 */
public class Average implements Statistic {

	@Override
	public double stat(double[] xs) {
		return StatisticsUtilities.average(xs);
	}
}
