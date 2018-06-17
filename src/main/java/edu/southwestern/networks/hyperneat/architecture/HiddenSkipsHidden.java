package edu.southwestern.networks.hyperneat.architecture;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * Architecture that contains 2 hidden layers each with 1 substrate. The first substrate is connected to the output as well as the second hidden layer.
 * This is for testing the efficacy of cascading architecture
 * @author DevonFulcher
 *
 */
public class HiddenSkipsHidden implements SubstrateArchitectureDefinition{

	/**
	 * TODO
	 */
	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		return networkHiddenArchitecture;
	}

	/**
	 * TODO
	 * @param inputSubstrateNames
	 * @param outputSubstrateNames
	 * @return
	 */
	private List<SubstrateConnectivity> getSubstrateConnectivity (List<String> inputSubstrateNames, List<String> outputSubstrateNames) {
		List<SubstrateConnectivity> substrateConnectivity = FlexibleSubstrateArchitecture.getDefaultConnectivity(inputSubstrateNames, outputSubstrateNames, getNetworkHiddenArchitecture());
		substrateConnectivity.add(new SubstrateConnectivity("process(0,0)", outputSubstrateNames.get(0), SubstrateConnectivity.CTYPE_FULL));
		return substrateConnectivity;
	}
	
	/**
	 * TODO
	 */
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask task) {
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(task);
		return getSubstrateConnectivity(io.t1, io.t2);
	}
}
