package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.mario.MarioCPPNtoGANLevelBreederTask;
import edu.southwestern.util.datastructures.ArrayUtil;
/**
 * Converts CPPN to GAN to Direct to GAN.
 * 
 * Cannot specific type of phenotype since it changes
 *
 */
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
	
	/**
	 * Gets the Long Vector Result from the MarioCPPNtoGANLevelBreederTask
	 * @return an array of doubles containing the Long Vector Result
	 */
	protected double[] getLongVectorResultFromCPPN(Network cppn) {
		return MarioCPPNtoGANLevelBreederTask.createLatentVectorFromCPPN(cppn, ArrayUtil.doubleOnes(cppn.numInputs()), Parameters.parameters.integerParameter("marioGANLevelChunks"));
	}
}

