package edu.southwestern.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.EitherOrGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.util.datastructures.ArrayUtil;
/**
 * Converts CPPN to GAN to Direct to GAN.
 * 
 * Cannot specific type of phenotype since it changes
 *
 */
@SuppressWarnings("rawtypes")
public class ConvertMarioCPPN2GANtoDirect2GANMutation extends ConvertCPPN2GANtoDirect2GANMutation {
	/**
	 * Construct that defines the rate (0.1) and tells if it's out of bounds
	 */
	public ConvertMarioCPPN2GANtoDirect2GANMutation() {
		super();
		double rate = Parameters.parameters.doubleParameter("indirectToDirectTransitionRate");
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}
	@SuppressWarnings({ "unchecked" })
	@Override
	/**
	 * Uses a CPPN to create a long latent vector.
	 * Assumes transition from CPPN2GAN format to Direct2GAN.
	 * Since phenotype changes, the type cannot be specified as a type parameter.
	 * 
	 * @param genotype a genotype specified by the user
	 */
	public void mutate(Genotype genotype) {
		// Cannot do a transition mutation on a genotype that has already transitioned!
		if(!((CPPNOrDirectToGANGenotype) genotype).getFirstForm()) return;
		// Save to assume phenotype is a network at this point
		Network cppn = (Network) genotype.getPhenotype();
		Genotype cppnOrDirect2ganGenotype = (CPPNOrDirectToGANGenotype) genotype;
		double[] longResult = MarioCPPNtoGANLevelBreederTask.createLatentVectorFromCPPN(cppn, ArrayUtil.doubleOnes(cppn.numInputs()), Parameters.parameters.integerParameter("marioGANLevelChunks"));

		RealValuedGenotype k = new RealValuedGenotype(longResult);
		//k.newInstance();
		((EitherOrGenotype<TWEANN, ArrayList<Double>>) cppnOrDirect2ganGenotype).switchForms(k);
		
	
	}

}

