package edu.southwestern.networks.hyperneat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * Defines methods to add hidden layers to HyperNEAT via cascade 
 * @author Devon Fulcher
 */
public class CascadeNetworks {
	/**
	 * Adds a new layer in between the previous last hidden layer and the output layer with
	 * connectivity from the previous last hidden layer to this new last hidden layer as 
	 * capableOfConvolution and connectivity from this new last hidden layer to the output
	 * layer as being fully connected
	 * @param originalHiddenArchitecture hidden architecture that a new layer will be added to
	 * @param originalConnectivity connectivity that new connectivity will be added to
	 * @param outputSubstrateNames unique names of the output substrates
	 * @param newLayerWidth the width(the number of substrates) of the new layer
	 * @param newSubstratesWidth the width of each substrate that is added
	 * @param newsubstratesHeight the height of each substrate that is added
	 * @param capableOfConvolution if true and convolution:true in batch file then 
	 * 		the layer between the previous last hidden layer and new last hidden layer
	 * 		will be convolutional otherwise it will be fully connected
	 * @return a deep copy of the previous architecture and connectivity with the new layer added
	 */
	public static Pair<List<Triple<Integer, Integer, Integer>>, List<Triple<String, String, Boolean>>> cascadeExpansion (
			List<Triple<Integer, Integer, Integer>> originalHiddenArchitecture,
			List<Triple<String, String, Boolean>> originalConnectivity,
			List<String> outputSubstrateNames,
			int newLayerWidth, int newSubstratesWidth, int newsubstratesHeight, boolean capableOfConvolution) {
		//create new hidden architecture
		List<Triple<Integer, Integer, Integer>> newArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		for(Triple<Integer, Integer, Integer> layer : originalHiddenArchitecture) {
			newArchitecture.add(layer.copy());
		}
		newArchitecture.add(new Triple<Integer, Integer, Integer>(newLayerWidth, newSubstratesWidth, newsubstratesHeight));
		//create new hidden architecture end
		//create new connectivity
		List<Triple<String, String, Boolean>> newConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		for(Triple<String, String, Boolean> connection: originalConnectivity) {
			newConnectivity.add(connection.copy());
		}
		//connect new layer to last hidden layer
		int lastHiddenLayerLocation = originalHiddenArchitecture.size() - 1; //y location of last hidden layer and location of last hidden layer in originalHiddenArchitecture
		for(int i = 0; i < originalHiddenArchitecture.get(lastHiddenLayerLocation).t1; i++) { //originalHiddenArchitecture.get(lastHiddenLayerLocation).t1 = the width of the last hidden layer
			for(int j = 0; j < newLayerWidth; j++) {
				newConnectivity.add(new Triple<String, String, Boolean>(
						"process(" + i + "," + lastHiddenLayerLocation + ")", 
						"process(" + j + "," + (lastHiddenLayerLocation + 1) + ")", capableOfConvolution));
			}
		}
		//connect new layer to last hidden layer end
		//connect new layer to output layer
		for(int i = 0; i < newLayerWidth; i++) {
			for(String outputSubstrateName: outputSubstrateNames) {
				newConnectivity.add(new Triple<String, String, Boolean>(
						"process(" + i + "," + (lastHiddenLayerLocation + 1) + ")",
						outputSubstrateName, false));
			}
		}
		//connect new layer to output layer end
		//create new connectivity end
		return new Pair<List<Triple<Integer, Integer, Integer>>, List<Triple<String, String, Boolean>>>(newArchitecture, newConnectivity);
	}
	
	/**
	 * Adds a new layer in between the previous last hidden layer and the output layer with
	 * connectivity from the previous last hidden layer to this new last hidden layer as 
	 * fully connected and connectivity from this new last hidden layer to the output
	 * layer as being fully connected. The width and height of each substrate in new last hidden layer
	 * is the same as the previous last hidden layer
	 * @param originalHiddenArchitecture hidden architecture that a new layer will be added to
	 * @param originalConnectivity connectivity that new connectivity will be added to
	 * @param outputSubstrateNames unique names of the output substrates
	 * @param newLayerWidth the width(the number of substrates) of the new layer
	 * @return a deep copy of the previous architecture and connectivity with the new layer added
	 */
	public static Pair<List<Triple<Integer, Integer, Integer>>, List<Triple<String, String, Boolean>>> cascadeExpansion (
			List<Triple<Integer, Integer, Integer>> originalHiddenArchitecture,
			List<Triple<String, String, Boolean>> originalConnectivity,
			List<String> outputSubstrateNames,
			int newLayerWidth) {
		Triple<Integer, Integer, Integer> lastHiddenLayer = originalHiddenArchitecture.get(originalHiddenArchitecture.size() - 1);
		return cascadeExpansion (originalHiddenArchitecture, originalConnectivity, outputSubstrateNames, newLayerWidth,
				lastHiddenLayer.t2,  lastHiddenLayer.t3, false);
	}
}
