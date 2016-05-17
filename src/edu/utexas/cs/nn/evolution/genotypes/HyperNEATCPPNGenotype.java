package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.networks.TWEANN;

public class HyperNEATCPPNGenotype extends TWEANNGenotype {

	HyperNEATCPPNGenotype() {
		super(); 
		// Probably need some extra code here
	}
	
	public TWEANN getPhenotype() {
		
		// Definitely need to override this.
		// Use the CPPN to create a TWEANN controller for
		// the domain. Probably need domain information from 
		// MMNEAT.task. Cast to a HyperNEATTask
		
		return null;
	}
	
	public Genotype<TWEANN> newInstance() {
		
		// Probably need to override this using information
		// about the substrate and the domain.
		// Domain information is in MMNEAT.task,
		// but substrate information doesn't exist yet.
		// Cast to a HyperNEATTask
		
		return null;
	}
}
