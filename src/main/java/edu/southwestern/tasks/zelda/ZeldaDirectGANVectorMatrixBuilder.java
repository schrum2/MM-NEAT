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

	private double[] wholeVector;
	private int segmentLength;

	public ZeldaDirectGANVectorMatrixBuilder(double[] wholeVector, int segmentLength) {
		this.wholeVector = wholeVector;
		this.segmentLength = segmentLength;
	}
	
	@Override
	public double[] latentVectorAndMiscDataForPosition(int width, int height, int x, int y) {
		int startIndex = segmentLength*(y*width + x);
		double[] result = new double[segmentLength];
		System.arraycopy(wholeVector, startIndex, result, 0, segmentLength);
		return result;
	}

}
