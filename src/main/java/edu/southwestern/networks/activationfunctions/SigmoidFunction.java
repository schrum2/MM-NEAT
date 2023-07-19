package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class SigmoidFunction implements ActivationFunction {

	/**
	 * Safe function for sigmoid. Will behave the same as Math.exp within
	 * specified bound.
	 *
	 * @param x Function parameter
	 * @return value of sigmoid(x)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.sigmoid(x);
	}

	@Override
	public String name() {
		return "sigmoid";
	}
}
