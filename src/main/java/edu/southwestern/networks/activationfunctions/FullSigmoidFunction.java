package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class FullSigmoidFunction implements ActivationFunction {

	/**
	 * Standard sigmoid, but stretched to the range
	 * -1 to 1. This is an alternative to tanh, and is
	 * also used in the original Picbreeder.
	 * @param x parameter
	 * @return result
	 */
	@Override
	public double f(double x) {
		return (1.0 / (1+ActivationFunctions.safeExp(-x))) * 2.0 - 1.0;
	}
}
