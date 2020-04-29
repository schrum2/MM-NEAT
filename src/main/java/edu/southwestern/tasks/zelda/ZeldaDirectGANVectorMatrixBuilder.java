package edu.southwestern.tasks.zelda;

/**
 * Takes a massive linear vector at construction (such as a real-valued genotype) that 
 * contains all of the numbers that will be part of the 2D grid representation. Basically
 * just chops up/returns subsections of the linear vector using row-major ordering
 * 
 * @author Jacob Schrum
 *
 */
public class ZeldaDirectGANVectorMatrixBuilder implements ZeldaGANVectorMatrixBuilder {

	private double[] wholeVector; //Input vector such as a real-valued genotype
	private int segmentLength;  

	/**
	 * Constructor that sets variables to be used to find the latent vector
	 * @param wholeVector Input vector
	 * @param segmentLength Length of one input segment (corresponding to individual room)
	 */
	public ZeldaDirectGANVectorMatrixBuilder(double[] wholeVector, int segmentLength) {
		this.wholeVector = wholeVector;
		this.segmentLength = segmentLength;
	}
	
	
	/**
	 * Creates latent vector to be sent to the GAN from the 
	 * variables that were initialized in the constructor. 
	 * Is accessing sub-range of the whole genome vector that
	 * the GAN can use to create a room.
	 * Returns the latent vector as an array of doubles 
	 */
	@Override
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y) {
		int startIndex = segmentLength*(y*width + x);
		double[] result = new double[segmentLength]; //resets size of result to be the same length
		System.arraycopy(wholeVector, startIndex, result, 0, segmentLength);
		return result;
	}

}
