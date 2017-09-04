package edu.southwestern.util.stats;

/**
 * Compute the maximum of an array of doubles
 * 
 * @author Jacob Schrum
 */
public class Max implements Statistic {

	@Override
	public double stat(double[] xs) {
		return StatisticsUtilities.maximum(xs);
	}
}
