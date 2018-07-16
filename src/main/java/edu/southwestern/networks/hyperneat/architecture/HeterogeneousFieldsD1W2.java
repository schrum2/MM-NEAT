package edu.southwestern.networks.hyperneat.architecture;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * A custom hyperneat architecture with heterogeneous receptive fields with a depth of 2 and width of 1. Connected with
 * receptive fields 3x3 and 5x5. These substrates are actually not in the same layer but are connected as if they were.
 * @author Devon Fulcher 
 */
public class HeterogeneousFieldsD1W2 implements SubstrateArchitectureDefinition{

	@Override
	public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture() {
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 6, 16));
		networkHiddenArchitecture.add(new Triple<Integer, Integer, Integer>(1, 8, 18));
		return networkHiddenArchitecture;
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask hnt) {
		List<SubstrateConnectivity> substrateConnectivity = new ArrayList<SubstrateConnectivity>();
		Pair<List<String>, List<String>> io = FlexibleSubstrateArchitecture.getInputAndOutputNames(hnt);
		List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture = getNetworkHiddenArchitecture();
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, networkHiddenArchitecture, 5, 5, 0);
		FlexibleSubstrateArchitecture.connectInputToHidden(substrateConnectivity, io.t1, networkHiddenArchitecture, 3, 3, 1);
		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, networkHiddenArchitecture, -1, -1, 0);
		FlexibleSubstrateArchitecture.connectHiddenToOutput(substrateConnectivity, io.t2, networkHiddenArchitecture, -1, -1, 1);
		return substrateConnectivity;
	}

}