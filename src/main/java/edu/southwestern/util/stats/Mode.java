package edu.southwestern.util.stats;

/**
 * Compute statistical mode (most common element) of an array of doubles.
 * 
 * @author Jacob Schrum
 */
public class Mode implements Statistic {

	@Override
	public double stat(double[] xs) {
		return StatisticsUtilities.mode(xs);
	}
}
