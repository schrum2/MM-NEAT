package edu.utexas.cs.nn.util.stats;

/**
 * Interface for a class that calculates a specific statistic
 * on an array of doubles.
 * @author Jacob Schrum
 */
public interface Statistic {

    public double stat(double[] xs);
}
