package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * @author Devon Fulcher
 */

public class FlexibleSubstrateArchitecture {

	/**
	 * @param hnt the hyperNEATTask
	 * @return A list of triples where the length of the list is equal to the depth of the number of hidden layers.
	 *  Each index of the list is a new layer. Index 0 is the first hidden layer.
	 *	The first param of each triple is the number of substrates in that corresponding layer. 
	 *	The second param is the width of each substrate. The third param is the height of each substrate.
	 *	Output and input layers are excluded because those are defined by the task. 
	 */
	public static List<Triple<Integer, Integer, Integer>> getHiddenArchitecture(HyperNEATTask hnt) {
		List<Triple<Integer, Integer, Integer>> hiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		List<Substrate> substrateInformation = hnt.getSubstrateInformation();	
		int yCoordCount = 0; //this will equal the number of substrates per layer when it is added to hiddenArchitecture
		int previousSubstrateType = Substrate.INPUT_SUBSTRATE;
		int previousYCoordinate = -1, previousWidth = -1, previousHeight = -1; //these are invalid values. they are just here to initialize.
		int currentSubstrateType, currentWidth, currentHeight, currentYCoordinate;
		Iterator<Substrate> it_substrateInformation = substrateInformation.iterator();
		while (it_substrateInformation.hasNext()) {
			Substrate currentSubstrate = it_substrateInformation.next(); 
			currentSubstrateType = currentSubstrate.getStype();
			currentYCoordinate = currentSubstrate.getSubLocation().t2;
			currentWidth = currentSubstrate.getSize().t1;
			currentHeight = currentSubstrate.getSize().t2;
			if (previousSubstrateType == Substrate.PROCCESS_SUBSTRATE && currentYCoordinate != previousYCoordinate) {
				assert previousWidth > 0 && previousHeight > 0 && yCoordCount > 0;
				hiddenArchitecture.add(new Triple<Integer, Integer, Integer>(yCoordCount, previousWidth, previousHeight));
				yCoordCount = 0;
			}
			if (currentSubstrateType == Substrate.PROCCESS_SUBSTRATE) {
				yCoordCount++;
			}
			previousSubstrateType = currentSubstrateType;
			previousYCoordinate = currentYCoordinate;
			previousWidth = currentWidth;
			previousHeight = currentHeight;
		}
		assert hiddenArchitecture.size() > 0;
		return hiddenArchitecture;
	}

	/**
	 * @param hnt the HyperNEATTask
	 * @return List of all substrate connections as given by a Triple where the first String is the unique identifier
	 * 	for a particular input/process substrate, the second String is the unique identifier for a particular ouput/process
	 *  substrate, and the boolean is true if the connectivity is capable of being convolutional and false otherwise.
	 */
	public static List<Triple<String, String, Boolean>> getAllSubstrateConnectivity(HyperNEATTask hnt) {
		Pair<List<String>,List<String>> inputAndOutputNames = getInputAndOutputNames(hnt);
		return getSubstrateConnectivity(inputAndOutputNames.t1, inputAndOutputNames.t2, getHiddenArchitecture(hnt));
	}

	/**
	 * gets the input and output names from the hyperNEATTask
	 * @param hnt theHyperNEATTask
	 * @return the input and output names stored in a pair. looks like (input names, output names)
	 */
	public static Pair<List<String>,List<String>> getInputAndOutputNames(HyperNEATTask hnt) {
		List<Substrate> substrateInformation = hnt.getSubstrateInformation();
		List<String> inputSubstrateNames = new ArrayList<String>();
		List<String> outputSubstrateNames = new ArrayList<String>();
		for (Substrate substrate: substrateInformation) {
			if (substrate.getStype() == Substrate.INPUT_SUBSTRATE) {
				inputSubstrateNames.add(substrate.getName());
			} else if (substrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				outputSubstrateNames.add(substrate.getName());
			}
		}
		return new Pair<List<String>, List<String>>(inputSubstrateNames, outputSubstrateNames);
	}

	/**
	 * @param inputSubstrateNames List of input substrate names
	 * @param outputSubstrateNames List of output substrate names
	 * @return List of adjacent substrate connections as given by a Triple where the first String is the unique identifier
	 * 	of a particular input/process substrate, the second String is the unique identifier of a particular output/process
	 *  substrate. Boolean is true if substrates are capable of being convolutional.
	 */
	public static List<Triple<String, String, Boolean>> getSubstrateConnectivity(
			List<String> inputSubstrateNames, 
			List<String> outputSubstrateNames, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture) {
		List<Triple<String, String, Boolean>> networkConnectivity = new ArrayList<Triple<String, String, Boolean>>();

		//connects input layer to first hidden/process layer, convolutional because boolean = true
		addFirstLayerConnectivity(networkConnectivity, inputSubstrateNames, networkHiddenArchitecture, true);

		//connects adjacent, possibly convolutional hidden layers
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
		int firstHiddenLayerWidth = (networkHiddenArchitecture.size() > 0)? networkHiddenArchitecture.get(0).t1: 0;
		for (String in: inputSubstrateNames) {
			for (int i = 0; i < firstHiddenLayerWidth; i++) {
				networkConnectivity.add(new Triple <String, String, Boolean>
				(in, "process(" + i + ",0)", capableOfConvolution));
			}
		}
		assert networkConnectivity.size() > 0;
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
		int lastHiddenLayerWidth = (networkHiddenArchitecture.size() > 0)? networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1: 0; 
		for (int i = 0; i < lastHiddenLayerWidth; i++) {
			for (String out: outputSubstrateNames) {
				networkConnectivity.add(new Triple<String, String, Boolean>
				("process(" + i + "," + (networkHiddenArchitecture.size() - 1) + ")", out, capableOfConvolution));
			}
		}
	}
}

