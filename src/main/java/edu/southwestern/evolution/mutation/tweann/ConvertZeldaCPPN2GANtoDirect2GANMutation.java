package edu.southwestern.evolution.mutation.tweann;

import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.CPPNOrDirectToGANGenotype;
import edu.southwestern.evolution.genotypes.EitherOrGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.TWEANN;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
import edu.southwestern.util.random.RandomNumbers;
/**
 * Converts CPPN to GAN to Direct to GAN.
 * 
 * Cannot specific type of phenotype since it changes
 *
 */
@SuppressWarnings("rawtypes")
public class ConvertZeldaCPPN2GANtoDirect2GANMutation extends Mutation {
	protected double rate;
	/**
	 * Construct that defines the rate (0.1) and tells if it's out of bounds
	 */
	public ConvertZeldaCPPN2GANtoDirect2GANMutation() {
		double rate = Parameters.parameters.doubleParameter("indirectToDirectTransitionRate");
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}
	@Override
	/**
	 * checks if it can perform the action 
	 * (random number < rate (0.1))
	 */
	public boolean perform() {
		return (RandomNumbers.randomGenerator.nextDouble() < rate);
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
		//EitherOrGenotype.switchForms(cppnOrDirect2ganGenotype);
		double[] inputMultipliers = new double[cppn.numInputs()];
		for(int i = 0;i<cppn.numInputs();i++) {
			inputMultipliers[i] = 1.0;
		}
		
		ZeldaCPPNtoGANVectorMatrixBuilder builder = new ZeldaCPPNtoGANVectorMatrixBuilder(cppn, inputMultipliers);
		int height = Parameters.parameters.integerParameter("cppn2ganHeight");
		int width = Parameters.parameters.integerParameter("cppn2ganWidth");

		int segmentLength = (GANProcess.latentVectorLength()+ZeldaCPPNtoGANLevelBreederTask.numberOfNonLatentVariables());
		double[] longResult = new double[segmentLength*height*width];
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				double[] vector = builder.latentVectorAndMiscDataForPosition(width, height, x, y);
				
				int nextIndex = vector.length*(y*height+x);
				System.arraycopy(vector, 0, longResult, nextIndex, vector.length);
				
			}
		}
		BoundedRealValuedGenotype k = new BoundedRealValuedGenotype(longResult, MMNEAT.getLowerBounds(), MMNEAT.getUpperBounds());
		//k.newInstance();
		((EitherOrGenotype<TWEANN, ArrayList<Double>>) cppnOrDirect2ganGenotype).switchForms(k);
		
	
	}

}
