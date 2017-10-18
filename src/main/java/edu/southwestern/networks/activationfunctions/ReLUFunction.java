package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class ReLUFunction implements ActivationFunction {
	/**
	 * ramp function, analogous to half-wave rectification in electrical engineering
	 * @param sum input
	 * @return result
	 */
	@Override
	public double f(double sum) {
		return Math.max(0, sum);
	}
	
	@Override
	public Activation equivalentDL4JFunction() {
		return Activation.RELU;
	}

	@Override
	public String name() {
		return "ReLU"; // "Rectified Linear Unit";
	}
}
