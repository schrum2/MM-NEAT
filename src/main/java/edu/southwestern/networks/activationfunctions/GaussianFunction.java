package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

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
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "gauss";
	}
}
