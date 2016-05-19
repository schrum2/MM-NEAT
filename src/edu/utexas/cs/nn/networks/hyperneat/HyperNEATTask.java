package edu.utexas.cs.nn.networks.hyperneat;

import java.util.List;

import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.util.datastructures.Pair;

/**
 * A task that HyperNEAT can be applied to.
 * Need to get substrate defining info from the domain.
 * 
 * The sensor and output labels from NetworkTask correspond 
 * to the inputs and outputs of the CPPN, not the phenotype.
 * 
 * @author schrum2
 *
 */
public interface HyperNEATTask extends NetworkTask {

        /**
         * Method that returns a list of information about
         * the substrate layers contained in the network.
         * @return List of Substrates in order from inputs to hidden to output layers
         */
	public List<Substrate> getSubstrateInformation();

        /**
         * Each Substrate has a unique String name, and this method returns
         * a list of String pairs indicating which Substrates are connected:
         * The Substrate from the first in the pair has links leading into the
         * neurons in the Substrate second in the pair.
         * @return Last of String pairs where all Strings are names of Substrates
         * for the domain.
         */
	public List<Pair<String,String>> getSubstrateConnectivity();
}
