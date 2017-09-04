package edu.southwestern.networks;

/**
 * Interface for neural network controllers.
 *
 * @author Jacob Schrum
 */
public interface Network {

	/**
	 * Number of nodes in input layer
	 * 
	 * @return number of inputs
	 */
	public int numInputs();

	/**
	 * Number of nodes in output layer (includes nodes from all modes and
	 * preference neurons)
	 * 
	 * @return number of outputs
	 */
	public int numOutputs();

	/**
	 * Returns the number of output signals that represent a network action,
	 * e.g. after a single mode has been chosen to represent the network.
	 * 
	 * @return number of outputs needed to define an action.
	 */
	public int effectiveNumOutputs();

	/**
	 * Returns the resulting outputs for the given inputs, after mode
	 * arbitration is done pre: inputs.length == numInputs()
	 * 
	 * @param inputs
	 *            Array of sensor inputs
	 * @return Array of network outputs (length == effectiveNumOutputs())
	 **/
	public double[] process(double[] inputs);

	/**
	 * Clear any internal state
	 */
	public void flush();

	/**
	 * Is the network a multitask network?
	 * 
	 * @return Whether network makes use of a multitask selection scheme
	 **/
	public boolean isMultitask();

	/**
	 * Used with multitask networks to designate the mode to use.
	 * 
	 * @param mode
	 *            = Mode to use, chosen by a multitask scheme
	 **/
	public void chooseMode(int mode);

	/**
	 * Report what the last mode used by the network was, and -1 if the net has
	 * not been used before
	 * 
	 * @return last used mode
	 */
	public int lastModule();

	/**
	 * Output of a specific mode after the previous processing
	 * 
	 * @param mode
	 *            Mode/module to query
	 * @return output from that mode/module (length == effectiveNumOutputs())
	 */
	public double[] moduleOutput(int mode);

	/**
	 * Number of modes the network has
	 * 
	 * @return number of modes/modules
	 */
	public int numModules();

	/**
	 * Get record of how many times each module was used
	 * 
	 * @return array where each position is the number of times that module was
	 *         used.
	 */
	public int[] getModuleUsage();
}
