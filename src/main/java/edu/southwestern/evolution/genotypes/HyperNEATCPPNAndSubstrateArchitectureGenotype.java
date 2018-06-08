package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
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
	 * defines a HyperNEATCPPNAndSubstrateArchitectureGenotype from the HyperNEAT task
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype() {
		this((HyperNEATTask) MMNEAT.task);
	}

	/**
	 * defines a HyperNEATCPPNAndSubstrateArchitectureGenotype from the HyperNEAT task
	 * @param hnt the HyperNEATTask
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(HyperNEATTask hnt) {
		this(FlexibleSubstrateArchitecture.getHiddenArchitecture(hnt), FlexibleSubstrateArchitecture.getAllSubstrateConnectivity(hnt));
	}

	/**
	 * defines a HyperNEATCPPNAndSubstrateArchitectureGenotype from the hiddenArchitecture and substrate connectivity
	 * @param hiddenArchitecture List of triples that specifies each substrate with the index of each triple being its layer.
	 * 		Each triple looks like (width of layer, width of substrate, height of substrate)
	 * @param networkHiddenConnections list of triples that specifies connectivity of network.
	 * 		Looks like (source substrate, target substrate, capable of convolution)
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(List<Triple<Integer, Integer, Integer>> hiddenArchitecture, List<Triple<String, String, Boolean>> allSubstrateConnectivity) {
		super();
		this.hiddenArchitecture = hiddenArchitecture;
		this.allSubstrateConnectivity = allSubstrateConnectivity;
		assert this.hiddenArchitecture != null;
	}

	/**
	 * defines a HyperNEATCPPNAndSubstrateArchitectureGenotype from the HyperNEAT task and substrate architecture definition
	 * @param HNTask the HyperNEAT task
	 * @param substrateArchitectureDefinition defines the hidden architecture and the connectivity of each layer
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(HyperNEATTask HNTask, SubstrateArchitectureDefinition substrateArchitectureDefinition) {
		super();
		this.hiddenArchitecture = substrateArchitectureDefinition.getNetworkHiddenArchitecture();
		assert this.hiddenArchitecture != null;
		Pair<List<String>,List<String>> inputAndOutputNames = FlexibleSubstrateArchitecture.getInputAndOutputNames(HNTask);
		this.allSubstrateConnectivity = substrateArchitectureDefinition.getSubstrateConnectivity(inputAndOutputNames.t1, inputAndOutputNames.t2);
	}
	
	/**
	 * Constructor for random hyperNEATCPPNGenotype.
	 * @param networkInputs
	 *            number of network inputs
	 * @param networkOutputs
	 *            number of network outputs
	 * @param archetypeIndex
	 *            index of genotype in archetype
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(int networkInputs, int networkOutputs, int archetypeIndex) {
		// Construct new CPPN with random weights
		super(networkInputs, networkOutputs, archetypeIndex);
		this.hiddenArchitecture = FlexibleSubstrateArchitecture.getHiddenArchitecture((HyperNEATTask) MMNEAT.task);
		this.allSubstrateConnectivity = FlexibleSubstrateArchitecture.getAllSubstrateConnectivity((HyperNEATTask) MMNEAT.task);
	}
	
	/**
	 * TODO
	 * @param archetypeIndex
	 * @param links
	 * @param genes
	 * @param outputNeurons
	 * @param hiddenArchitecture
	 * @param allSubstrateConnectivity
	 */
	private HyperNEATCPPNAndSubstrateArchitectureGenotype(int archetypeIndex, ArrayList<LinkGene> links, ArrayList<NodeGene> genes, int outputNeurons,
			List<Triple<Integer, Integer, Integer>> hiddenArchitecture, List<Triple<String, String, Boolean>> allSubstrateConnectivity) {
		super(archetypeIndex, links, genes, outputNeurons);
		this.allSubstrateConnectivity = allSubstrateConnectivity;
		this.hiddenArchitecture = hiddenArchitecture;
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
	 * a deep copy of the calling HyperNEATCPPNAndSubstrateArchitectureGenotype
	 * @return the deep copy
	 */
	@Override
	public Genotype<TWEANN> copy() {
		List<Triple<Integer, Integer, Integer>> copyHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		for(Triple<Integer, Integer, Integer> layer: this.hiddenArchitecture) {
			copyHiddenArchitecture.add(layer);
		}
		List<Triple<String, String, Boolean>> copyAllSubstrateConnectivity = new ArrayList<Triple<String, String, Boolean>>();
		for(Triple<String, String, Boolean> connectionId: this.allSubstrateConnectivity) {
			copyAllSubstrateConnectivity.add(connectionId);
		}
		ArrayList<LinkGene> copyLinks = new ArrayList<LinkGene>(this.links.size());
		for (LinkGene lg : this.links) {// needed for a deep copy
			copyLinks.add(newLinkGene(lg.sourceInnovation, lg.targetInnovation, lg.weight, lg.innovation, false));
		}
		ArrayList<NodeGene> copyGenes = new ArrayList<NodeGene>(this.nodes.size());
		for (NodeGene ng : this.nodes) {// needed for a deep copy
			copyGenes.add(newNodeGene(ng.ftype, ng.ntype, ng.innovation, false, ng.getBias()));
		}
		return new HyperNEATCPPNAndSubstrateArchitectureGenotype(this.archetypeIndex, copyLinks, copyGenes, this.numOut, copyHiddenArchitecture, copyAllSubstrateConnectivity);
	}

	@Override
	public Genotype<TWEANN> newInstance() {
		return new HyperNEATCPPNAndSubstrateArchitectureGenotype(HyperNEATUtil.numCPPNInputs(), HyperNEATUtil.numCPPNOutputs(), this.archetypeIndex); 
	}
}
