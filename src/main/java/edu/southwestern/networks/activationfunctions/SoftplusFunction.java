package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class SoftplusFunction implements ActivationFunction {
	/**
	 * The smooth approximation of the reLU function
	 * @param sum
	 * @return
	 */
	@Override
	public double f(double sum) {
		return Math.log(1 + Math.pow(Math.E, sum));
	}

	@Override
	public Activation equivalentDL4JFunction() {
		// Need to verify that this really is the same as what DL4J does
		return Activation.SOFTPLUS;
	}

	@Override
	public String name() {
		return "softplus";
	}
}
