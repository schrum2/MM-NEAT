package edu.utexas.cs.nn.networks.hyperneat;

import java.util.List;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * A task that HyperNEAT can be applied to. Need to get substrate defining info
 * from the domain.
 *
 * @author schrum2
 *
 */
public interface HyperNEATTask {

	// Inputs: X1, Y1, X2, Y2, and Bias
	public static final int DEFAULT_NUM_CPPN_INPUTS = 5;
	public static final int INDEX_X1 = 0; // source
	public static final int INDEX_Y1 = 1; // source
	public static final int INDEX_X2 = 2; // target
	public static final int INDEX_Y2 = 3; // target
	public static final int INDEX_BIAS = 4;

	/**
	 * The task designates how many CPPN inputs there will be.
	 * In most cases, this will probably be 5, to correspond to
	 * X1, Y1, X2, Y2, and Bias. However, for 1D substrates, we
	 * can drop two inputs when we get rid of either the x or y
	 * dimension. It's even possible this may be expanded to 3D
	 * substrates in the future.
	 * 
	 * @return Number of CPPN inputs
	 */
	public int numCPPNInputs();

	/**
	 * The HyperNEATCPPNGenotype class generates inputs to the CPPN, but
	 * it does so under the assumption of 2D substrates. Therefore, the
	 * input set contains x and y coordinates for the source and target
	 * substrates, and a 5th input for the bias. However, if 1D substrates
	 * are being used with a reduced number of CPPN inputs, then some of
	 * these inputs need to be removed. Most classes can implement this
	 * method by simply returning the original input, but in some cases
	 * a filtered result must be returned.
	 * 
	 * @param fullInputs Original inputs: X1, Y1, X2, Y2, BIAS
	 * @return Possibly filtered version of original inputs
	 */
	public double[] filterCPPNInputs(double[] fullInputs);
	
	/**
	 * Method that returns a list of information about the substrate layers
	 * contained in the network.
	 *
	 * @return List of Substrates in order from inputs to hidden to output
	 *         layers
	 */
	public List<Substrate> getSubstrateInformation();

	/**
	 * Each Substrate has a unique String name, and this method returns a list
	 * of String pairs indicating which Substrates are connected: The Substrate
	 * from the first in the pair has links leading into the neurons in the
	 * Substrate second in the pair.
	 *
	 * @return Last of String pairs where all Strings are names of Substrates
	 *         for the domain.
	 */
	public List<Pair<String, String>> getSubstrateConnectivity();
}
