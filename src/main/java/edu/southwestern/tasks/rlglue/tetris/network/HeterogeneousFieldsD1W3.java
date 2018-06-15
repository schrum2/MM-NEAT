package edu.southwestern.tasks.rlglue.tetris.network;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HeterogeneousFieldsD1W3 implements SubstrateArchitectureDefinition{

	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 8, 18));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 6, 16));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 10, 20));
		return networkHiddenArchitecture;
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask hnt) {
		List<SubstrateConnectivity> substrateConnectivity = new ArrayList<SubstrateConnectivity>();
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(hnt);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, getNetworkHiddenArchitecture(), 5, 5);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, getNetworkHiddenArchitecture(), 3, 3);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, getNetworkHiddenArchitecture(), 1, 1);
//		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, getNetworkHiddenArchitecture(), -1, -1, 0);
//		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, getNetworkHiddenArchitecture(), -1, -1, 1);
//		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, getNetworkHiddenArchitecture(), -1, -1, 2);
		return substrateConnectivity;
	}

}
