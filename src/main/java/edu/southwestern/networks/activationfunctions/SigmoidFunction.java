package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

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
	public Activation equivalentDL4JFunction() {
		return Activation.SIGMOID;
	}

	@Override
	public String name() {
		return "sigmoid";
	}
}
