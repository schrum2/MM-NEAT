package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateArchitectureDefinition;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class HyperNEATCPPNAndSubstrateArchitectureGenotype extends HyperNEATCPPNGenotype {
	// Describes the sequence of hidden layers. The input and output layers are still provided by the HyperNEATTask.
	// List of triples that specifies each substrate with the index of each triple being its layer.
	// Each triple looks like (width of layer, width of substrate, height of substrate)
	List<Triple<Integer, Integer, Integer>> hiddenArchitecture;
	// Describes connectivity between ALL substrates, including the input and output substrates
	// list of triples that specifies connectivity of network.
	// Looks like (source substrate, target substrate, capable of convolution)
	List<Triple<String, String, Boolean>> allSubstrateConnectivity;

	/**
	 * @param hiddenArchitecture List of triples that specifies each substrate with the index of each triple being its layer.
	 * 		Each triple looks like (width of layer, width of substrate, height of substrate)
	 * @param networkHiddenConnections list of triples that specifies connectivity of network.
	 * 		Looks like (source substrate, target substrate, capable of convolution)
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(List<Triple<Integer, Integer, Integer>> hiddenArchitecture, List<Triple<String, String, Boolean>> allSubstrateConnectivity) {
		super();
		this.hiddenArchitecture = hiddenArchitecture;
		this.allSubstrateConnectivity = allSubstrateConnectivity;
	}

	public HyperNEATCPPNAndSubstrateArchitectureGenotype(SubstrateArchitectureDefinition SubstrateArchitectureDefinition) {

	}

	/**
	 * @param HNTask the HyperNEATTask
	 * @return list of substrates with predefined hidden architecture
	 */
	@Override
	public List<Substrate> getSubstrateInformation(HyperNEATTask HNTask) {
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
		int numLayers = hiddenArchitecture.size();
		for (int i = 0; i < numLayers; i++) {
			Triple<Integer, Integer, Integer> currentLayer = hiddenArchitecture.get(i);
			for (int j = 0; j < hiddenArchitecture.get(i).t1; j++) {
				newSubstrateInformation.add(new Substrate(
						new Pair<>(currentLayer.t2, currentLayer.t3), Substrate.PROCCESS_SUBSTRATE, 
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
	 * @return all the connectivity between links. list of triples that specifies connectivity of network.
	 * Looks like (source substrate, target substrate, capable of convolution)
	 */
	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity(HyperNEATTask HNTask) {
		return allSubstrateConnectivity;
	}

	/**
	 * sets allSubstrateConnectivity to be a network that is fully connected between each layer and
	 * is defined by the hiddenArchitecture
	 * @param HNTask the HyperNEATTask
	 */
	public void setAdjacentSubstrateConnectivity(HyperNEATTask HNTask) {
		List<Triple<String, String, Boolean>> substrateConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		List<Substrate> oldSubstrateInformation = HNTask.getSubstrateInformation();
		Iterator<Substrate> subItr = oldSubstrateInformation.iterator();
		//connects input layer to first hidden layer
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
		int numSubsInFirstHiddenLayer = hiddenArchitecture.get(0).t1;
		for (int i = 0; i < numInputs; i++) {
			for (int j = 0; j < numSubsInFirstHiddenLayer; i++) {
				substrateConnectivity.add(new Triple <String, String, Boolean>
				("Input(" + i + ")", "process(" + i + ",0)", false));
			}
		}
		//connects input layer to first hidden layer end
		//connects adjacent hidden layers
		int numLayers = hiddenArchitecture.size();
		for (int i = 0; i < numLayers - 1; i++) {
			for (int src = 0; src < hiddenArchitecture.get(i).t1; src++) {
				for (int target = 0; target < hiddenArchitecture.get(i + 1).t1; target++) {
					substrateConnectivity.add(new Triple<String, String, Boolean>(
							"process(" + src + "," + i + ")", "process(" + target + "," + (i + 1) + ")", false));
				}
			}
		}
		//connects adjacent hidden layers end
		//connects last hidden layer to output layer
		int numOutputs = 0;
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				numOutputs++;
			} 
		}
		//hiddenArchitecture.get(hiddenArchitecture.size() - 1).t1 = width of the last hidden layer
		for (int i = 0; i < hiddenArchitecture.get(hiddenArchitecture.size() - 1).t1; i++) {
			for (int j = 0; j < numOutputs; j++) {
				substrateConnectivity.add(new Triple<String, String, Boolean>
				("process(" + i + "," + (hiddenArchitecture.size() - 1) + ")", "", false));
			}
		}
		//connects last hidden layer to output layer end
	}
}
