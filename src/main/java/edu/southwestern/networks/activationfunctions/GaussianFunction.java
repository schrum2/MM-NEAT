package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class GaussianFunction implements ActivationFunction {

	/**
	 * Overloaded gaussian function for x. Sigma is set to 1 and mu is set to 0.
	 * Does not utilize safe exp at the moment, can be changed.
	 *
	 * @param x Function parameter
	 * @return value of gaussian(x)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.gaussian(x, 1, 0);
	}

	@Override
	public String name() {
		return "gauss";
	}
}
