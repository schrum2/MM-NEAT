package edu.southwestern.tasks.zelda;

import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
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
		
		// TODO: Assume  zeldaGANLevelWidthChunks and zeldaGANLevelHeightChunks were specified, but define cppn2ganWidth and cppn2ganHeight to take on their values here
		Parameters.parameters.setInteger("cppn2ganWidth", Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks"));
		Parameters.parameters.setInteger("cppn2ganHeight", Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks"));

	}
	
	/**
	 * This method sets a point in 2D space based on the location information from the parameters, creates a vector of inputs, 
	 * and then sends that vector through the CPPN to get a latent vector.
	 * Returns a latent vector to be passed into the GAN from the CPPN
	 */
	@Override
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y) {
		return latentVectorAndMiscDataForPosition(width,height,x,y,inputMultipliers,cppn);
	}
	
	/**
	 * TODO
	 * Sets a point in 2D space
	 * creates a vector of inputs
	 * sends that vector through the CPPN to get a latent vector
	 * returns a latent vector to be passed into the GAN from the CPPN
	 * @param width the width of a map/level
	 * @param height the height of a map/level
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param inputMultipliers Can remove inputs with 0 values, or include them with values of 1
	 * @param cppn the cppn
	 * @return vector the latent vector
	 */
	public static double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y, double[] inputMultipliers, Network cppn) {
		ILocated2D scaled = CartesianGeometricUtilities.centerAndScale(new Tuple2D(x, y), width, height); // sets a point in 2D space 
		//sets input vector based on the point created and a bias
		double[] remixedInputs = { scaled.getX(), scaled.getY(), scaled.distance(new Tuple2D(0, 0)) * GraphicsUtil.SQRT2, GraphicsUtil.BIAS };
		// Might turn some inputs on/off
		for(int i = 0; i < remixedInputs.length; i++) {
			remixedInputs[i] *= inputMultipliers[i];
		}
		double[] vector = cppn.process(remixedInputs); // runs input through the CPPN to get the correct latent vector
		return vector;
	}

}
