package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class SquareWaveFunction implements ActivationFunction {
	/**
	 * Generalization of square wave function with only one parameter.
	 * 
	 * @param x function parameter
	 * @return value of squareWave(x, 1, 1)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.squareWave(x, 1, 1);
	}

	@Override
	public String name() {
		return "square"; //"Square Wave";
	}
}
