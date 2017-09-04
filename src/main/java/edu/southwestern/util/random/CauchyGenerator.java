package edu.southwestern.util.random;

/**
 * Random number generator to create Cauchy distributed values.
 * 
 * @author Jacob Schrum
 */
public class CauchyGenerator implements RandomGenerator {

	@Override
	public double randomOutput() {
		return RandomNumbers.randomCauchyValue();
	}

}
