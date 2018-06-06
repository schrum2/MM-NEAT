package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Triple;

public class FlexibleSubstrateArchitecture {
	
	/**
	 * @param inputSubstrateNames List of input substrate names
	 * @param outputSubstrateNames List of output substrate names
	 * @return List of all substrate connections as given by a Triple where the first String is the unique identifier
	 * 	of a particular input/process substrate, the second String is the unique identifier of a particular output/process
	 *  substrate. The boolean is true for each layer except the last two, which are fully connected(boolean is true if substrates
	 *  are capable of being convolutional).
	 */
	public static List<Triple<String, String, Boolean>> getSubstrateConnectivity(
			List<String> inputSubstrateNames, 
			List<String> outputSubstrateNames, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture) {
		List<Triple<String, String, Boolean>> networkConnectivity = new ArrayList<Triple<String, String, Boolean>>();

		//connects input layer to first hidden/process layer, convolutional because boolean = true
		addFirstLayerConnectivity(networkConnectivity, inputSubstrateNames, networkHiddenArchitecture, true);

		//connects adjacent convolutional hidden layers
		addHiddenLayerConnectivity(networkConnectivity, networkHiddenArchitecture);

		//connects last hidden/process layer to output layer, fully connected because boolean = false
		addLastLayerConnectivity(networkConnectivity, outputSubstrateNames, networkHiddenArchitecture, false);

		return networkConnectivity;
	}

	/**
	 * connects input layer to first hidden/process layer
	 * @param networkConnectivity list that connectivity is appended to
	 * @param inputSubstrateNames list of each input substrate name
	 * @param networkHiddenArchitecture architecture of hidden layers
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private static void addFirstLayerConnectivity(
			List<Triple<String, String, Boolean>> networkConnectivity, 
			List<String> inputSubstrateNames, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture, 
			boolean capableOfConvolution) {
		for (String in: inputSubstrateNames) {
			for (int i = 0; i < networkHiddenArchitecture.get(0).t1; i++) {
				networkConnectivity.add(new Triple <String, String, Boolean>
				(in, "process(" + i + ",0)", capableOfConvolution));
			}
		}
	}

	/**
	 * connects two adjacent hidden substrate layers
	 * @param networkConnectivity list that connectivity is appended to
	 * @param networkHiddenArchitecture architecture of hidden layers
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private static void addHiddenLayerConnectivity(
			List<Triple<String, String, Boolean>> networkConnectivity, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture) {
		int stride = Parameters.parameters.integerParameter("stride");
		int numLayers = networkHiddenArchitecture.size();
		for (int i = 0; i < numLayers - 1; i++) {
			for (int src = 0; src < networkHiddenArchitecture.get(i).t1; src++) {
				for (int target = 0; target < networkHiddenArchitecture.get(i + 1).t1; target++) {
					// Need to generalize this more later: Currently, we assume convolution will be used if the next layer up is exactly the
					// right size to allow it without zero padding.
					boolean capableOfConvolution = networkHiddenArchitecture.get(i).t2 + 2*stride == networkHiddenArchitecture.get(i+1).t2 &&
											       networkHiddenArchitecture.get(i).t3 + 2*stride == networkHiddenArchitecture.get(i+1).t3;
					networkConnectivity.add(new Triple<String, String, Boolean>("process(" + src + "," + i + ")", "process(" + target + "," + (i + 1) + ")", capableOfConvolution));
				}
			}
		}
	}


	/**
	 * TODO: addPoolingHiddenLayerConnectivity
	 */


	/**
	 * connects last hidden layer to output layer
	 * @param networkConnectivity list that connectivity is appended to
	 * @param outputSubstrateNames list of each output substrate name
	 * @param lastHiddenLayerWidth the width of the last hidden layer
	 * @param capableOfConvolution if true and if parameter convolution = true in batch file
	 * 		then this layer will be convolutional else it will be fully connected
	 */
	private static void addLastLayerConnectivity(
			List<Triple<String, String, Boolean>> networkConnectivity, 
			List<String> outputSubstrateNames,
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture,
			boolean capableOfConvolution) {
		//networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1 = width of the last hidden layer
		for (int i = 0; i < networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1; i++) {
			for (String out: outputSubstrateNames) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				("process(" + i + "," + (networkHiddenArchitecture.size() - 1) + ")", out, capableOfConvolution));
			}
		}
	}
}

