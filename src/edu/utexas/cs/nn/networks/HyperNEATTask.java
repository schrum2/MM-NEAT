package edu.utexas.cs.nn.networks;

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

	// Method that returns a list of information about
	// the substrate layers contained in the network
	//public List<????> getSubstrateInformation();
	
	//
}
