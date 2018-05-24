package edu.southwestern.networks.hyperneat;

import java.util.List;

import edu.southwestern.util.datastructures.Triple;

public interface SubstrateArchitectureDefinition {
		
		/**
		 *  A list of triples where length of the list is equal to the depth of the number of hidden layers.
		 *  Each index of the list is a new layer. Index 0 is the first hidden layer.
		 *	The first param of each triple is the number of substrates in that corresponding layer. 
		 *	The second param is the width of each substrate. The third param is the height of each substrate.
		 *	
		 *	Output and input layers are excluded excluded because those are defined by the task. 
		 */
		public List<Triple<Integer, Integer, Integer>> getNetworkHiddenArchitecture();
		
				
}
