package edu.southwestern.networks.hyperneat;

import java.util.List;

import edu.southwestern.util.datastructures.Triple;

public interface SubstrateArchitectureDefinition {
		
		/**
		 * A list of triples where length of the list is equal to the depth of network.
		 * Each index of the list is a new layer. Index 0 is the first/input layer.
		 	The first param of each triple is the number of substrates in that corresponding layer, 
			The second param is the width of the substrate. The third is the height of the substrate
		 */
		public List<Triple<Integer, Integer, Integer>> getNetworkArchitecture();
				
}
