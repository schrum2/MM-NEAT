package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.EitherOrGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.util.random.RandomNumbers;

import java.util.*;

@SuppressWarnings("rawtypes")
public abstract class ConvertCPPN2GANtoDirect2GANMutation extends Mutation {

	protected double rate;

	public ConvertCPPN2GANtoDirect2GANMutation() {
		super();
	}

	@Override
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
	}

	/**
	 * Uses a CPPN to create a long latent vector.
	 * Assumes transition from CPPN2GAN format to Direct2GAN.
	 * Since phenotype changes, the type cannot be specified as a type parameter.
	 * 
	 * @param genotype a genotype specified by the user
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public void mutate(Genotype genotype) {
		// Cannot do a transition mutation on a genotype that has already transitioned!
		if(!((CPPNOrDirectToGANGenotype) genotype).getFirstForm()) return;
		// Save to assume phenotype is a network at this point
		Network cppn = (Network) genotype.getPhenotype();
		Genotype cppnOrDirect2ganGenotype = (CPPNOrDirectToGANGenotype) genotype;
		double[] longResult = getLongVectorResultFromCPPN(cppn);

		RealValuedGenotype k = new RealValuedGenotype(longResult);
		//k.newInstance();
		((EitherOrGenotype<TWEANN, ArrayList<Double>>) cppnOrDirect2ganGenotype).switchForms(k);
		
	
	}

	protected abstract double[] getLongVectorResultFromCPPN(Network cppn);
	
}