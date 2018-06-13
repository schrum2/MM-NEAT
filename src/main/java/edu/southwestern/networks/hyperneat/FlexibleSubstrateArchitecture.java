package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.MiscUtil;
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
	 * @return List of the SubstrateConnectivity of the network
	 */
	public static List<SubstrateConnectivity> getAllSubstrateConnectivity(HyperNEATTask hnt) {
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
	 * @return List of SubstrateConnectivity of the network
	 */
	public static List<SubstrateConnectivity> getSubstrateConnectivity(
			List<String> inputSubstrateNames, 
			List<String> outputSubstrateNames, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture) {
		List<SubstrateConnectivity> networkConnectivity = new ArrayList<SubstrateConnectivity>();
		//connects input layer to first hidden/process layer
		addFirstLayerConnectivity(networkConnectivity, inputSubstrateNames, networkHiddenArchitecture, SubstrateConnectivity.CTYPE_CONVOLUTION);

		//connects adjacent, possibly convolutional hidden layers
		addHiddenLayerConnectivity(networkConnectivity, networkHiddenArchitecture);

		//connects last hidden/process layer to output layer
		addLastLayerConnectivity(networkConnectivity, outputSubstrateNames, networkHiddenArchitecture, SubstrateConnectivity.CTYPE_FULL);
		
		return networkConnectivity;
	}

	/**
	 * connects input layer to first hidden/process layer
	 * @param networkConnectivity list that connectivity is appended to
	 * @param inputSubstrateNames list of each input substrate name
	 * @param networkHiddenArchitecture architecture of hidden layers
	 * @param connectivityType how these two substrates are connected(i.e. full, convolutional,...)
	 */
	private static void addFirstLayerConnectivity(
			List<SubstrateConnectivity> networkConnectivity, 
			List<String> inputSubstrateNames, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture, 
			int connectivityType) {
		int firstHiddenLayerWidth = (networkHiddenArchitecture.size() > 0)? networkHiddenArchitecture.get(0).t1: 0;
		for (String in: inputSubstrateNames) {
			for (int i = 0; i < firstHiddenLayerWidth; i++) {
				networkConnectivity.add(new SubstrateConnectivity
				(in, "process(" + i + ",0)", connectivityType));
			}
		}
		assert networkConnectivity.size() > 0;
	}

	/**
	 * connects two adjacent hidden substrate layers
	 * @param networkConnectivity list that connectivity is appended to
	 * @param networkHiddenArchitecture architecture of hidden layers
	 * @param CONNECTIVITY_TYPE how these two substrates are connected (i.e. full, convolutional,...)
	 */
	private static void addHiddenLayerConnectivity(
			List<SubstrateConnectivity> networkConnectivity, 
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture) {
		int stride = Parameters.parameters.integerParameter("stride");
		int numLayers = networkHiddenArchitecture.size();
		for (int i = 0; i < numLayers - 1; i++) {
			for (int src = 0; src < networkHiddenArchitecture.get(i).t1; src++) {
				for (int target = 0; target < networkHiddenArchitecture.get(i + 1).t1; target++) {
					// Need to generalize this more later: Currently, we assume convolution will be used if the next layer up is exactly the
					// right size to allow it without zero padding.
					int connectivityType = (networkHiddenArchitecture.get(i).t2 + 2*stride == networkHiddenArchitecture.get(i+1).t2 &&
							networkHiddenArchitecture.get(i).t3 + 2*stride == networkHiddenArchitecture.get(i+1).t3)? SubstrateConnectivity.CTYPE_FULL: SubstrateConnectivity.CTYPE_CONVOLUTION;
					networkConnectivity.add(new SubstrateConnectivity("process(" + src + "," + i + ")", "process(" + target + "," + (i + 1) + ")", connectivityType));
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
	 * @param connectivityType how these two substrates are connected (i.e. full, convolutional,...)
	 */
	private static void addLastLayerConnectivity(
			List<SubstrateConnectivity> networkConnectivity, 
			List<String> outputSubstrateNames,
			List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture,
			int connectivityType) {
		int lastHiddenLayerWidth = (networkHiddenArchitecture.size() > 0)? networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1: 0; 
		for (int i = 0; i < lastHiddenLayerWidth; i++) {
			for (String out: outputSubstrateNames) {
				networkConnectivity.add(new SubstrateConnectivity
				("process(" + i + "," + (networkHiddenArchitecture.size() - 1) + ")", out, connectivityType));
			}
		}
	}
}

