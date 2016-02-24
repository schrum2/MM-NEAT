package edu.utexas.cs.nn.networks;

/**
 *
 * @author Jacob Schrum
 */
public interface Network {
    
    /**
     * Number of nodes in input layer
     **/
    public int numInputs();

    /**
     * Number of nodes in output layer (includes nodes from all modes and preference neurons)
     **/
    public int numOutputs();
    
    /**
     * Returns the number of output signals that represent a network action,
     * e.g. after a single mode has been chosen to represent the network.
     **/
    public int effectiveNumOutputs();
    
    /**
     * Returns the resulting outputs for the given inputs, after mode arbitration is done
     * @param inputs Array of sensor inputs
     * @return Array of network outputs
     **/
    public double[] process(double[] inputs);
    
    /**
     * Clear any internal state
     **/
    public void flush();
    
    /**
     * Is the network a multitask network?
     * @return Whether network makes use of a multitask selection scheme
     **/
    public boolean isMultitask();

    /**
     * Used with multitask networks to designate the mode to use.
     * @param mode = Mode to use, chosen by a multitask scheme
     **/
    public void chooseMode(int mode);
    
    /**
     * Report what the last mode used by the network was, and -1 if the net
     * has not been used before
     * @return last used mode
     */
    public int lastMode();
    
    /**
     * Output of a specific mode after the previous processing
     * @param mode
     * @return 
     */
    public double[] modeOutput(int mode);
    
    /**
     * Number of modes the network has
     * @return 
     */
    public int numModes();
}
