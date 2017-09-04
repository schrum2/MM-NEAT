package edu.southwestern.util.stats;

/**
 * Compute minimum of an array of doubles
 * 
 * @author Jacob Schrum
 */
public class Min implements Statistic {

	@Override
	public double stat(double[] xs) {
		return StatisticsUtilities.minimum(xs);
	}
}
