package edu.southwestern.networks;

/**
 * An interface for any class that helps determine which mode/module a multitask
 * network or similar architecture should use. Essentially, this is an interface
 * for providing human-specified task divisions. This class needs to be
 * implemented by specific classes for each domain, so that a means of making
 * the decision based on domain information can be defined.
 *
 * @author Jacob Schrum
 */
public interface ModeSelector {

	/**
	 * Which mode to use.
	 * 
	 * @return An int from 0 and up specifying the mode/module
	 */
	public int mode();

	/**
	 * Total number of modes/modules available
	 * 
	 * @return An int for the number of modes/modules.
	 */
	public int numModes();

	/**
	 * Used for resetting any state information
	 */
	public void reset();
}
