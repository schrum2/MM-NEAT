package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.hyperneat.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HyperNEATCPPNAndSubstrateArchitectureGenotype extends HyperNEATCPPNGenotype {
	List<Triple<Integer, Integer, Integer>> networkHiddenArchitecture;
	List<Triple<String, String, Boolean>> networkHiddenConnections;
	
	//designates which connections are convolutional.
	//must be greater than or equal to elements in this array as there are connections
	final boolean[] CONVOLUTIONAL_CONNECTIONS = {false};

	/**
	 * 
	 * @param hiddenArchitecture
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(List<Triple<Integer, Integer, Integer>> hiddenArchitecture) {
		super();
		this.networkHiddenArchitecture = hiddenArchitecture;
	}

	/**
	 * inputs and outputs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	List<Substrate> getSubstrateInformation(HyperNEATTask HNTask) {
		List<Substrate> newSubstrateInformation = new LinkedList<Substrate>();
		List<Substrate> oldSubstrateInformation = HNTask.getSubstrateInformation();
		Iterator<Substrate> subItr = oldSubstrateInformation.iterator();
		//substrate input layer information
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.INPUT_SUBSTRATE) {
				newSubstrateInformation.add(currentSubstrate);
			} else if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				break; // Break out once output substrate is found
			}
			// Nothing occurs when hidden substrate is found. We just skip them.
		}
		//substrate input layer information end
		//substrate hidden layer information
		int numLayers = networkHiddenArchitecture.size();
		for (int i = 0; i < numLayers; i++) {
			Triple<Integer, Integer, Integer> currentLayer = networkHiddenArchitecture.get(i);
			for (int j = 0; j < networkHiddenArchitecture.get(i).t1; j++) {
				newSubstrateInformation.add(new Substrate(
						new Pair(currentLayer.t2, currentLayer.t3), Substrate.PROCCESS_SUBSTRATE, 
						new Triple<Integer, Integer, Integer> (j , i + 1, 0),
						"process(" + j + "," + i + ")"));
			}
		}
		//substrate hidden layer information end
		//substrate output layer information
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				newSubstrateInformation.add(currentSubstrate);
			} 
		}
		//substrate output layer information end		
		return newSubstrateInformation;
	}

	/**
	 * inputs and outputs
	 */
	@Override
	List<Triple<String, String, Boolean>> getSubstrateConnectivity(HyperNEATTask HNTask) {
		List<Triple<String, String, Boolean>> substrateConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		List<Substrate> oldSubstrateInformation = HNTask.getSubstrateInformation();
		
		Iterator<Substrate> subItr = oldSubstrateInformation.iterator();

		int numInputs = 0;
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.INPUT_SUBSTRATE) {
				numInputs++;
			} else if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				break; // Break out once output substrate is found
			}
			// Nothing occurs when hidden substrate is found. We just skip them.
		}
		int numSubsInFirstHiddenLayer = networkHiddenArchitecture.get(0).t1;
		for (int i = 0; i < numInputs; i++) {
			for (int j = 0; j < numSubsInFirstHiddenLayer; i++) {
				substrateConnectivity.add(new Triple <String, String, Boolean>
				("Input(" + i + ")", "process(" + i + ",0)", CONVOLUTIONAL_CONNECTIONS[0]));
			}
		}
		
		int numLayers = networkHiddenArchitecture.size();
		for (int i = 0; i < numLayers - 1; i++) {
			for (int src = 0; src < networkHiddenArchitecture.get(i).t1; src++) {
				for (int target = 0; target < networkHiddenArchitecture.get(i + 1).t1; target++) {
					substrateConnectivity.add(new Triple<String, String, Boolean>("process(" + src + "," + i + ")", "process(" + target + "," + (i + 1) + ")", CONVOLUTIONAL_CONNECTIONS[i]));
				}
			}
		}
		
		
		int numOutputs = 0;
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				numOutputs++;
			} 
		}
		//networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1 = width of the last hidden layer
		// FIX IN A MOMENT
//		for (int i = 0; i < networkHiddenArchitecture.get(networkHiddenArchitecture.size() - 1).t1; i++) {
//			for (int j = 0; j < numOutputs; j++) {
//				substrateConnectivity.add(new Triple<String, String, Boolean>
//				("process(" + i + "," + (networkHiddenArchitecture.size() - 1) + ")", out, CONVOLUTIONAL_CONNECTIONS));
//			}
//		}
//		
		return substrateConnectivity;
	}
}
