package edu.utexas.cs.nn.evolution;

import edu.utexas.cs.nn.tasks.Task;

/**
 * Interface for any EA that progresses by generations. Essentially the opposite
 * of a steady-state EA.
 * 
 * @author Jacob Schrum
 */
public interface GenerationalEA {

	/**
	 * Current generation being evaluated
	 * 
	 * @return Number for tracking current generation
	 */
	public int currentGeneration();

	/**
	 * Number of times that an individual is evaluated, where a single
	 * evaluation can possibly consist of several trials.
	 * 
	 * @return number of evaluations in the generation
	 */
	public int evaluationsPerGeneration();

	/**
	 * Task being evolved in
	 * 
	 * @return Task/domain
	 */
	public Task getTask();
}
