package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.networks.TWEANN;

// TODO: Javadoc
public class CPPNOrDirectToGANGenotype extends EitherOrGenotype<TWEANN,ArrayList<Double>> {

	// TODO: Javadoc
	
	public CPPNOrDirectToGANGenotype() {
		this(new TWEANNGenotype(), true);
	}	
	
	// TODO: Javadoc
	public CPPNOrDirectToGANGenotype(Genotype genotype, boolean firstForm) {
		super(genotype, firstForm);
	}
	
	// TODO: Javadoc
	@Override
	public void mutate() {
		// new WhateverMutationOpIsCalled().go( params )
		// TODO: Small chance of transitioning from CPPN to Direct
		// Put into the mutation operation: super.firstForm = false;
		super.mutate();
	}
	

}
