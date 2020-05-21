package edu.southwestern.evolution.genotypes;

import java.util.ArrayList;

import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.random.RandomNumbers;

public class CPPNOrDirectToGANGenotypeMutation extends Mutation<ArrayList<Double>>{
	protected double rate;
	
	public CPPNOrDirectToGANGenotypeMutation(String rateName) {
		this(Parameters.parameters.doubleParameter(rateName));

	}
	public CPPNOrDirectToGANGenotypeMutation(double rate) {
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;	
		}
	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

	@Override
	public void mutate(Genotype<ArrayList<Double>> genotype) {
		Network cppn = (Network) genotype.getPhenotype();
//input mult same length as cppn but all ones		
	}

}
