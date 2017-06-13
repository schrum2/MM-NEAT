package edu.utexas.cs.nn.evolution.genotypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class OffsetHybrIDGenotype extends HyperNEATCPPNGenotype {

	RealValuedGenotype rvg;

	public OffsetHybrIDGenotype() {
		super();
		rvg = new RealValuedGenotype(HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()));
	}

	public OffsetHybrIDGenotype(HyperNEATCPPNGenotype hngt) {
		this(hngt.links, hngt.nodes, hngt.neuronsPerModule);
	}
	
	public OffsetHybrIDGenotype(ArrayList<LinkGene> links, ArrayList<NodeGene> genes, int outputNeurons) {
		super(links, genes, outputNeurons);
		rvg = new RealValuedGenotype(HyperNEATUtil.totalPossibleLinks(HyperNEATUtil.getHyperNEATTask()));
	}

	@Override
	public TWEANN getPhenotype() {
		TWEANNGenotype genotype = (TWEANNGenotype) super.getSubstrateGenotype(HyperNEATUtil.getHyperNEATTask()).copy();
		//offsets to all links accessed through RealValuedGenotype
		ArrayList<Double> offsets = rvg.getPhenotype();
		//substrate connectivity of HyperNEATTask used to access all source and target substrate indexes
		List<Triple<String, String, Boolean>> connections = HyperNEATUtil.getHyperNEATTask().getSubstrateConnectivity();
		//substrate info used to access all source and target substrates at obtained indexes
		List<Substrate> subs = HyperNEATUtil.getHyperNEATTask().getSubstrateInformation();
		HashMap<String, Integer> substrateIndexMapping = new HashMap<String, Integer>();
		for (int i = 0; i < subs.size(); i++) {
			substrateIndexMapping.put(subs.get(i).getName(), i);
		}	
		//instead of looping through offsets, use an index that updates regardless of case in for loop
		int offsetIndex = 0;
		//loop through all substrates
		for (int j = 0; j < connections.size(); j++) { // For each pair of substrates that are connected
			int sourceSubstrateIndex = substrateIndexMapping.get(connections.get(j).t1);
			int targetSubstrateIndex = substrateIndexMapping.get(connections.get(j).t2);
			Substrate sourceSubstrate = subs.get(sourceSubstrateIndex);
			Substrate targetSubstrate = subs.get(targetSubstrateIndex);
			// This loop goes through every (x,y) coordinate in Substrate s1: source substrate
			for(Pair<Integer,Integer> src : sourceSubstrate.coordinateList()) {
				int fromXIndex = src.t1;
				int fromYIndex = src.t2;
				// If the neuron in the source substrate is dead, it will not have outputs
				if(!sourceSubstrate.isNeuronDead(fromXIndex, fromYIndex)) {
					// This loop searches through every (x,y) coordinate in Substrate s2: target substrate
					for(Pair<Integer,Integer> target: targetSubstrate.coordinateList()) {
						int targetXindex = target.t1;
						int targetYIndex = target.t2;
						// If the target neuron is dead, then don't bother with incoming links
						if(!targetSubstrate.isNeuronDead(targetXindex, targetYIndex)) {
							long sourceID = getInnovationID(fromXIndex, fromYIndex, sourceSubstrateIndex, subs);
							long targetID = getInnovationID(targetXindex, targetYIndex, targetSubstrateIndex, subs);		
							LinkGene link = getLinkBetween(sourceID, targetID); //calculate link between source and target
							double offset = offsets.get(offsetIndex);
							link.weight += offset; //manipulate link weight based on offset at current index
							offsetIndex++; //increment offsetIndex
						}
					}
				}
			}
		}
		// Modify genotype
		return genotype.getPhenotype();
	}

	@Override
	public Genotype<TWEANN> copy() {
		HyperNEATCPPNGenotype initialCopy = (HyperNEATCPPNGenotype) super.copy(); 
		RealValuedGenotype rvgCopy = (RealValuedGenotype) rvg.copy();
		OffsetHybrIDGenotype result = new OffsetHybrIDGenotype(initialCopy);
		result.rvg = rvgCopy;
		return result;
	}

	@Override
	public Genotype<TWEANN> newInstance() {
		HyperNEATCPPNGenotype initialInstance = (HyperNEATCPPNGenotype) super.newInstance();
		RealValuedGenotype rvgInstance = (RealValuedGenotype) rvg.newInstance();
		OffsetHybrIDGenotype result = new OffsetHybrIDGenotype(initialInstance);
		result.rvg = rvgInstance;
		return result;

	}
}
