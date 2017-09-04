package edu.southwestern.util.random;

/**
 * Random number generator to create Gaussian distributed values.
 * 
 * @author Jacob Schrum
 */
public class GaussianGenerator implements RandomGenerator {

	@Override
	public double randomOutput() {
		return RandomNumbers.randomGenerator.nextGaussian();
	}

}
