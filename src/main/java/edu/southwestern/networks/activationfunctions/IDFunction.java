package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

public class IDFunction implements ActivationFunction {

	/**
	 * Returns input value
	 */
	@Override
	public double f(double x) {
		return x;
	}

	@Override
	public Activation equivalentDL4JFunction() {
		return Activation.IDENTITY;
	}

	@Override
	public String name() {
		return "id";
	}
}
