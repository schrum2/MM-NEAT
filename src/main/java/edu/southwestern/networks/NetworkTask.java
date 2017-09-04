package edu.southwestern.networks;

/**
 * Interface for tasks that evolve a neural network.
 * 
 * These methods are primarily used for post-evaluation displays. Labels for the
 * network inputs sensors and output neurons are required.
 * 
 * @author Jacob Schrum
 */
public interface NetworkTask {

	/**
	 * Labels for each network input. Length needs to match the number of input
	 * neurons in networks, and the number of inputs agents receive.
	 * 
	 * @return String array of input labels
	 */
	public String[] sensorLabels();

	/**
	 * Labels for each network output. Length needs to match the number of
	 * output neurons per module of networks.
	 * 
	 * @return String array of output labels.
	 */
	public String[] outputLabels();
}
