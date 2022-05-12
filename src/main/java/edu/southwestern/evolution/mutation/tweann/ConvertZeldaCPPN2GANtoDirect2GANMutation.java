package edu.southwestern.evolution.mutation.tweann;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.interactive.gvgai.ZeldaCPPNtoGANLevelBreederTask;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.tasks.zelda.ZeldaCPPNtoGANVectorMatrixBuilder;
/**
 * Converts CPPN to GAN to Direct to GAN.
 * 
 * Cannot specific type of phenotype since it changes
 *
 */
public class ConvertZeldaCPPN2GANtoDirect2GANMutation extends ConvertCPPN2GANtoDirect2GANMutation {
	/**
	 * Construct that defines the rate (0.1) and tells if it's out of bounds
	 */
	public ConvertZeldaCPPN2GANtoDirect2GANMutation() {
		super();
		double rate = Parameters.parameters.doubleParameter("indirectToDirectTransitionRate");
		assert 0 <= rate && rate <= 1 : "Mutation rate out of range: " + rate;
		this.rate = rate;
	}
	
	/**
	 * Gets the Long Vector Result 
	 * @return longResult An array of doubles containing the Long Vector Result
	 */
	protected double[] getLongVectorResultFromCPPN(Network cppn) {
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
		return longResult;
	}
	
}
