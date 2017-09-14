package edu.southwestern.evolution.nsga2.bd.vectors;

/**
 * Stores information representing the behavior of a single genotype from simulation.
 * Used for Behavioral Diversity, and other divergent search approaches.
 *
 * @author Jacob Schrum
 */
public interface BehaviorVector {
	/**
	 * Distance in Behavior Space between this BehaviorVector and some other BehaviorVector
	 * @param rhs The other BehaviorVector
	 * @return Distance in behavior space
	 */
	public double distance(BehaviorVector rhs);
}
