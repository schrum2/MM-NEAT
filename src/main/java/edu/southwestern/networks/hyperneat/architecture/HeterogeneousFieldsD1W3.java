package edu.southwestern.networks.hyperneat.architecture;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HeterogeneousFieldsD1W3 implements SubstrateArchitectureDefinition{

	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		//Although each element of networkHiddenArchitecture is typically a layer, in this case they act as different substrates within the same layer
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 6, 16));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 8, 18));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		return networkHiddenArchitecture;
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask hnt) {
		List<SubstrateConnectivity> substrateConnectivity = new ArrayList<SubstrateConnectivity>();
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(hnt);
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = getNetworkHiddenArchitecture();
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, networkHiddenArchitecture, 5, 5, 0);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, networkHiddenArchitecture, 3, 3, 1);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, networkHiddenArchitecture, 1, 1, 2);
		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, networkHiddenArchitecture, -1, -1, 0);
		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, networkHiddenArchitecture, -1, -1, 1);
		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, networkHiddenArchitecture, -1, -1, 2);
		return substrateConnectivity;
	}
}
