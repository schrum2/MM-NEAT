package edu.southwestern.tasks.zelda;

import edu.southwestern.networks.Network;
import edu.southwestern.util.CartesianGeometricUtilities;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.util2D.ILocated2D;
import edu.southwestern.util.util2D.Tuple2D;

/**
 * This acts similarly to the Direct2GAN method, but the input for the GAN is
 * the output of a CPPN which is a latent vector. 
 *
 */
public class ZeldaCPPNtoGANVectorMatrixBuilder implements ZeldaGANVectorMatrixBuilder {

	private Network cppn;
	private double[] inputMultipliers;

	/**
	 * Constructor used to set variables 
	 * @param cppn Compositional Pattern Producing Network that generates the dungeon
	 * @param inputMultipliers Mask indicating how CPPN inputs should be treated (usually 1s or 0s for enabled/disabled)
	 */
	public ZeldaCPPNtoGANVectorMatrixBuilder(Network cppn, double[] inputMultipliers) {
		this.cppn = cppn;
		this.inputMultipliers = inputMultipliers; // weights associated with different inputs to the CPPN
	}
	
	/**
	 * This method sets a point in 2D space based on the location information from the parameters, creates a vector of inputs, 
	 * and then sends that vector through the CPPN to get a latent vector.
	 * Returns a latent vector to be passed into the GAN from the CPPN
	 */
	@Override
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), width, height); // sets a point in 2D space 
		//sets input vector based on the point created and a bias
		double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * GraphicsUtil.SQRT2, GraphicsUtil.BIAS };
		// vector multiplication on inputs with weights 
		// Might turn some inputs on/off
		for(int i = 0; i < remixedInputs.length; i++) {
			remixedInputs[i] *= inputMultipliers[i];
		}
		double[] vector = cppn.process(remixedInputs); // runs input through the CPPN to get the correct latent vector
		return vector;
	}

}
