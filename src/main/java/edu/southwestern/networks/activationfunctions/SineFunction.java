package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class SineFunction implements ActivationFunction {
	/**
	 * Sine function for x. Uses Math.sin();
	 *
	 * @param x Function parameter
	 * @return value of sin(x)
	 */
	@Override
	public double f(double x) {
		return Math.sin(x);
	}

	@Override
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "sin";
	}
}
