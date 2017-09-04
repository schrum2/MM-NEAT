package edu.southwestern.evolution.nsga2.tug;

/**
 * Tasks that use TUG must implement this interface
 *
 * @author He_Deceives
 */
public interface TUGTask {

	/**
	 * Gets and returns an array of doubles indicating the starting goals
	 * @return the starting goals
	 */
	public double[] startingGoals();
}
