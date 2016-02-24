package edu.utexas.cs.nn.util.stats;

/**
 *
 * @author Jacob Schrum
 */
public class Percentile implements Statistic {

    public double percentile;

    /*
     * 0 < percentile <= 100
     */
    public Percentile(double percentile) {
        this.percentile = percentile;
    }

    public double stat(double[] xs) {
        return StatisticsUtilities.percentile(xs, percentile);
    }
}
