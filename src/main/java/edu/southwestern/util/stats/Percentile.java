package edu.southwestern.util.stats;

/**
 * Compute value for a given percentile in an array of doubles: For example, the
 * 23rd percentile is the value that 23% of the values in the double array are
 * less than.
 * 
 * @author Jacob Schrum
 */
public class Percentile implements Statistic {

	public double percentile;

	/**
	 * Constructor that specifies percentile to calculate.
	 * 
	 * Pre: 0 < percentile <= 100
	 * 
	 * @param percentile
	 */
	public Percentile(double percentile) {
		this.percentile = percentile;
	}

	@Override
	public double stat(double[] xs) {
		return StatisticsUtilities.percentile(xs, percentile);
	}
}
