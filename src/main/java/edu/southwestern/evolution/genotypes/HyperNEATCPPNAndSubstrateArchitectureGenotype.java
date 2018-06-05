package edu.southwestern.evolution.genotypes;

import java.util.List;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.util.datastructures.Triple;

public class HyperNEATCPPNAndSubstrateArchitectureGenotype extends HyperNEATCPPNGenotype {
	List<Triple<Integer, Integer, Integer>> hiddenArchitecture;
	
	
	/**
	 * 
	 * @param hiddenArchitecture
	 */
	HyperNEATCPPNAndSubstrateArchitectureGenotype(List<Triple<Integer, Integer, Integer>> hiddenArchitecture) {
		this.hiddenArchitecture = hiddenArchitecture;
	}
	
	/**
	 * 
	 */
	@Override
	List<Substrate> getSubstrateInformation(HyperNEATTask HNTask) {
		return HNTask.getSubstrateInformation();
	}
	
	/**
	 * 
	 */
	@Override
	List<Triple<String, String, Boolean>> getSubstrateConnectivity(HyperNEATTask HNTask) {
		return HNTask.getSubstrateConnectivity();
	}
}
