package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.ActivationFunctions;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.networks.hyperneat.architecture.CascadeNetworks;
import edu.southwestern.networks.hyperneat.architecture.FlexibleSubstrateArchitecture;
import edu.southwestern.networks.hyperneat.architecture.SubstrateArchitectureDefinition;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

/**
 * @author Devon Fulcher
 */
public class HyperNEATCPPNAndSubstrateArchitectureGenotype extends HyperNEATCPPNGenotype {

	private static final long serialVersionUID = 6414177465555914122L;
	// Describes the sequence of hidden layers. The input and output layers are still provided by the HyperNEATTask.
	// List of triples that specifies each substrate with the index of each triple being its layer.
	// Each triple looks like (width of layer, width of substrate, height of substrate)
	public List<Triple<Integer, Integer, Integer>> hiddenArchitecture;
	// Describes connectivity between ALL substrates, including the input and output substrates
	// list of triples that specifies connectivity of network.
	// Looks like (source substrate, target substrate, connectivityType)
	public List<SubstrateConnectivity> allSubstrateConnectivity;

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
		this(FlexibleSubstrateArchitecture.getHiddenArchitecture(hnt), FlexibleSubstrateArchitecture.getDefaultConnectivity(hnt));
	}

	/**
	 * defines a HyperNEATCPPNAndSubstrateArchitectureGenotype from the hiddenArchitecture and substrate connectivity
	 * @param hiddenArchitecture List of triples that specifies each substrate with the index of each triple being its layer.
	 * 		Each triple looks like (width of layer, width of substrate, height of substrate)
	 * @param allSubstrateConnectivity how each substrate is connected
	 */
	public HyperNEATCPPNAndSubstrateArchitectureGenotype(List<Triple<Integer, Integer, Integer>> hiddenArchitecture, List<SubstrateConnectivity> allSubstrateConnectivity) {
		super();
		this.hiddenArchitecture = hiddenArchitecture;
		this.allSubstrateConnectivity = allSubstrateConnectivity;
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		assert !allSubstrateConnectivity.get(0).sourceSubstrateName.equals("null") : "How was a null string name constructed?";

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
		this.allSubstrateConnectivity = substrateArchitectureDefinition.getSubstrateConnectivity(HNTask);
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		assert !allSubstrateConnectivity.get(0).sourceSubstrateName.equals("null") : "How was a null string name constructed?";


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
		this.allSubstrateConnectivity = FlexibleSubstrateArchitecture.getDefaultConnectivity((HyperNEATTask) MMNEAT.task);
		assert allSubstrateConnectivity.size() > 0 : "allSubstrateConnectivity size must be greater than 0";
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		assert !allSubstrateConnectivity.get(0).sourceSubstrateName.equals("null") : "How was a null string name constructed?";


	}
	
	/**
	 * @param archetypeIndex index of genotype in archetype
	 * @param links edges of neural network
	 * @param genes nodes of neural network
	 * @param outputNeurons output nodes
	 * @param hiddenArchitecture List of triples that specifies each substrate with the index of each triple being its layer.
	 * 		Each triple looks like (width of layer, width of substrate, height of substrate)
	 * @param allSubstrateConnectivity how each substrate is connected
	 */
	private HyperNEATCPPNAndSubstrateArchitectureGenotype(int archetypeIndex, ArrayList<LinkGene> links, ArrayList<NodeGene> genes, int outputNeurons,
			List<Triple<Integer, Integer, Integer>> hiddenArchitecture, List<SubstrateConnectivity> allSubstrateConnectivity) {
		super(archetypeIndex, links, genes, outputNeurons);
		this.allSubstrateConnectivity = allSubstrateConnectivity;
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		assert !allSubstrateConnectivity.get(0).sourceSubstrateName.equals("null") : "How was a null string name constructed?";

		this.hiddenArchitecture = hiddenArchitecture;
	}
	
	/**
	 * @param hngt the HyperNEAT CPPN genotype
	 */
//	public HyperNEATCPPNAndSubstrateArchitectureGenotype(HyperNEATCPPNGenotype hngt) {
//		super(hngt.numIn, hngt.numOut, hngt.archetypeIndex);
//		HyperNEATTask task = (HyperNEATTask) MMNEAT.task;
//		this.hiddenArchitecture = FlexibleSubstrateArchitecture.getHiddenArchitecture(task);
//		this.allSubstrateConnectivity = FlexibleSubstrateArchitecture.getDefaultConnectivity(task);
//		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
//		assert !allSubstrateConnectivity.get(0).sourceSubstrateName.equals("null") : "How was a null string name constructed?";
//	}

	/**
	 * @param HNTask the HyperNEATTask
	 * @return list of substrates with predefined hidden architecture
	 */
	@Override
	public List<Substrate> getSubstrateInformation(HyperNEATTask HNTask) {
		List<Substrate> newSubstrateInformation = new LinkedList<Substrate>();
		List<Substrate> oldSubstrateInformation = HNTask.getSubstrateInformation();
		Iterator<Substrate> subItr = oldSubstrateInformation.iterator();
		Substrate firstOutputSubstrate = null;
		//substrate input layer information
		while(subItr.hasNext()) {
			Substrate currentSubstrate = subItr.next();
			if(currentSubstrate.getStype() == Substrate.INPUT_SUBSTRATE) {
				newSubstrateInformation.add(currentSubstrate);
			} else if(currentSubstrate.getStype() == Substrate.OUTPUT_SUBSTRATE) {
				firstOutputSubstrate = currentSubstrate; //this will be added to list after the hidden layers
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
		assert firstOutputSubstrate != null;
		newSubstrateInformation.add(firstOutputSubstrate);
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
	 * 		looks like (source substrate, target substrate, connectivityType)
	 */
	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity(HyperNEATTask HNTask) {
		assert allSubstrateConnectivity != null;
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		return allSubstrateConnectivity;
	}
	
	
	/**
	 * Adds a new layer in between the previous last hidden layer and the output layer with given specification
	 * @param newLayerWidth the width(the number of substrates) of the new layer
	 * @param newSubstratesWidth the width of each substrate that is added
	 * @param newSubstratesHeight the height of each substrate that is added
	 * @param connectivityType how these two substrates are connected (i.e. full, convolutional,...)
	 */
	public void cascadeExpansion (int newLayerWidth, int newSubstratesWidth, int newSubstratesHeight, int connectivityType) {
		//if HyperNEAT is encoded via MSS new outputs must be added to the cppn
		if (!Parameters.parameters.booleanParameter("substrateLocationInputs")) {
			int[] ftypes = new int[CommonConstants.leo ? newLayerWidth * 4 + newLayerWidth: newLayerWidth * 2 + newLayerWidth];
			for (int i = 0; i < ftypes.length; i++) {
				ftypes[i] = ActivationFunctions.randomFunction();
			}
			int numOutputsInPhenotype = FlexibleSubstrateArchitecture.getInputAndOutputNames(((HyperNEATTask) MMNEAT.task)).t2.size();
			addMSSNeuronsToCPPN(allSubstrateConnectivity.size(), hiddenArchitecture.size(), numOutputsInPhenotype, newLayerWidth, ftypes);
		}
		Pair<List<Triple<Integer, Integer, Integer>>, List<SubstrateConnectivity>> newDefiniton = 
				CascadeNetworks.cascadeExpansion(this.hiddenArchitecture, this.allSubstrateConnectivity, 
				FlexibleSubstrateArchitecture.getInputAndOutputNames((HyperNEATTask) MMNEAT.task).t2,
				newLayerWidth, newSubstratesWidth, newSubstratesHeight, connectivityType);
		this.hiddenArchitecture = newDefiniton.t1;
		this.allSubstrateConnectivity = newDefiniton.t2;
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
	}

	/**
	 * a deep copy of the calling HyperNEATCPPNAndSubstrateArchitectureGenotype
	 * @return the deep copy
	 */
	@Override
	public Genotype<TWEANN> copy() {
		int[] temp = moduleUsage;
		
		// Copy the architecture
		List<Triple<Integer, Integer, Integer>> copyHiddenArchitecture = new ArrayList<Triple<Integer, Integer, Integer>>();
		for(Triple<Integer, Integer, Integer> layer: this.hiddenArchitecture) {
			copyHiddenArchitecture.add(layer.copy());
		}
		List<SubstrateConnectivity> copyAllSubstrateConnectivity = new ArrayList<SubstrateConnectivity>();
		for(SubstrateConnectivity connectionId: this.allSubstrateConnectivity) {
			copyAllSubstrateConnectivity.add(connectionId.copy());
		}
		assert allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		assert copyAllSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		
		// Copy the link and node genes
		ArrayList<LinkGene> copyLinks = new ArrayList<LinkGene>(this.links.size());
		for (LinkGene lg : this.links) {// needed for a deep copy
			copyLinks.add(newLinkGene(lg.sourceInnovation, lg.targetInnovation, lg.weight, lg.innovation, false));
		}
		ArrayList<NodeGene> copyGenes = new ArrayList<NodeGene>(this.nodes.size());
		for (NodeGene ng : this.nodes) {// needed for a deep copy
			copyGenes.add(newNodeGene(ng.ftype, ng.ntype, ng.innovation, false, ng.getBias(), false)); // CPPN nodes are not normalized
		}
		// Construct the copy
		HyperNEATCPPNAndSubstrateArchitectureGenotype copy = new HyperNEATCPPNAndSubstrateArchitectureGenotype(
				this.archetypeIndex, copyLinks, copyGenes, this.numOut, copyHiddenArchitecture, copyAllSubstrateConnectivity);
		//TWEANNGenotype result = new TWEANNGenotype(this.getPhenotype());

		assert copy.allSubstrateConnectivity.get(0).sourceSubstrateName != null : "How was a null name constructed?";
		
		moduleUsage = temp;
		copy.moduleUsage = new int[temp.length];
		System.arraycopy(this.moduleUsage, 0, copy.moduleUsage, 0, moduleUsage.length);
		return copy;
	}

	/**
	 * creates a new instance of HyperNEATCPPNAndSubstrateArchitectureGenotype
	 */
	@Override
	public Genotype<TWEANN> newInstance() {
		HyperNEATCPPNAndSubstrateArchitectureGenotype copy = new HyperNEATCPPNAndSubstrateArchitectureGenotype(
				HyperNEATUtil.numCPPNInputs(), HyperNEATUtil.numCPPNOutputs(), this.archetypeIndex);
		copy.moduleUsage = new int[copy.numModules];
		return copy; 
	}
}
