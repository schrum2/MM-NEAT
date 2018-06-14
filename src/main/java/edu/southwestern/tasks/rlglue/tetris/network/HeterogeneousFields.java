package edu.southwestern.tasks.rlglue.tetris.network;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HeterogeneousFields implements SubstrateArchitectureDefinition{
	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 6, 16)); //5x5 receptive field
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 4, 14)); //3x3 receptive field
		return networkHiddenArchitecture;
	}
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask hnt) {
		List<SubstrateConnectivity> substrateConnectivity = new ArrayList<SubstrateConnectivity>();
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(hnt);
		FlexibleSubstrateArchitecture.addFirstLayerReceptiveField(substrateConnectivity, io.t1, getNetworkHiddenArchitecture(), 5, 5);
		FlexibleSubstrateArchitecture.addHiddenLayerConnectivity(substrateConnectivity, getNetworkHiddenArchitecture(), 3, 3);
		FlexibleSubstrateArchitecture.addLastLayerConnectivity(substrateConnectivity, io.t2, getNetworkHiddenArchitecture(), SubstrateConnectivity.CTYPE_FULL);
		return substrateConnectivity;
	}
}
