package edu.southwestern.tasks.rlglue.tetris.network;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.util.datastructures.Triple;

/**
 * Architecture that contains 2 hidden layers each with 1 substrate. The first substrate is connected to the output as well as the second hidden layer.
 * This is for testing the efficacy of cascading architecture
 * @author DevonFulcher
 *
 */
public class HiddenSkipsHidden implements SubstrateArchitectureDefinition{

	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		return null;
	}

	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity (List<String> inputSubstrateNames, List<String> outputSubstrateNames) {
		List<Triple<String, String, Boolean>> substrateConnectivity = FlexibleSubstrateArchitecture.getSubstrateConnectivity(inputSubstrateNames, outputSubstrateNames, getNetworkHiddenArchitecture());
		for(String out: outputSubstrateNames) {
			substrateConnectivity.add(new Triple<String, String, Boolean>("process(0,0)", out, false));
		}
		return substrateConnectivity;
	}
}
