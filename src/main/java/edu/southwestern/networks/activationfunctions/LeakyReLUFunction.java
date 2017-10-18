package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class LeakyReLUFunction implements ActivationFunction {
	/**
	 * returns the leaky rectified function, which allows for a small, non-zero gradient when the unit is not active
	 * @param sum input
	 * @return result
	 */
	@Override
	public double f(double sum) {
		return (sum > 0) ? sum : 0.01 * sum;
	}

	@Override
	public Activation equivalentDL4JFunction() {
		// The alpha of 0.01 is the default used in DL4J
		return Activation.LEAKYRELU;
	}

	@Override
	public String name() {
		return "ReLU(leaky)"; //"Leaky Rectified Linear Unit";
	}
}
