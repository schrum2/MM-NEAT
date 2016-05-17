package edu.utexas.cs.nn.evolution.genotypes;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.TWEANN;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;

public class HyperNEATCPPNGenotype extends TWEANNGenotype {

	HyperNEATCPPNGenotype() {
		super(); 
		// Probably need some extra code here
	}
	
	// Use the CPPN to create a TWEANN controller for
	// the domain. 
	public TWEANN getPhenotype() {
		
		HyperNEATTask hnt = (HyperNEATTask) MMNEAT.task;
		List<Substrate> subs = hnt.getSubstrateInformation();
		
		int numInputs = 0;
		int numHidden = 0;
		int numOutputs = 0;
		for(Substrate s: subs) {
			switch(s.stype) {
			case Substrate.STYPE_INPUT:
				numInputs += s.size.t1 * s.size.t2;
				break;
			case Substrate.STYPE_HIDDEN:
				numHidden += s.size.t1 * s.size.t2;
				break;
			case Substrate.STYPE_OUTPUT:
				numOutputs += s.size.t1 * s.size.t2;
				break;
			default:
				System.out.println("Should not happen!");
				// Print a stack trace?
				System.exit(1);
			}
		}
		
		ArrayList<NodeGene> nodes = null; // todo
		ArrayList<LinkGene> links = null; // todo
		int neuronsPerModule = -1; // todo
		
		TWEANNGenotype tg = new TWEANNGenotype(nodes, links, neuronsPerModule, false, false, -1);		
		// -1 will probably crash ... consider solutions 
		// set up archetype to handle -1 as a spacial value to ignore
		
		return tg.getPhenotype();
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
