package edu.southwestern.experiment;

/**
 * Interface for all experiments. Experiments can either be runs that conduct
 * evolution, or post-evolution evaluation of evolved agents. The interface
 * should be flexible enough to allow other possibilities as well.
 *
 * @author Jacob Schrum
 */
public interface Experiment {

	/**
	 * Called once at start of experiment
	 */
	public void init();

	/**
	 * Called once to start running the experiment
	 */
	public void run();

	/**
	 * This is available to be called inside the run() method at the appropriate
	 * time to supply a stop condition.
	 * 
	 * @return Whether the experiment should stop
	 */
	public boolean shouldStop();
}
