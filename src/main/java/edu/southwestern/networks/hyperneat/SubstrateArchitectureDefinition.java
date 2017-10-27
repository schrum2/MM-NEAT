package edu.southwestern.networks.hyperneat;

import java.util.List;

import edu.southwestern.util.datastructures.Triple;

public interface SubstrateArchitectureDefinition {
		
		/**
		 * A list of triples where length is depth of network, 
		 	first param of triple is the number of substrates in that layer, 
			the second is the width, and the third is the height
		
		 */
		public List<Triple<Integer, Integer, Integer>> getNetworkArchitecture();
		
}
