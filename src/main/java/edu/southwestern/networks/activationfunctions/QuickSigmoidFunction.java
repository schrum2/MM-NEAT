package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class QuickSigmoidFunction implements ActivationFunction {

	/**
	 * Quick approximation to sigmoid. Inaccurate, but has needed properties.
	 * Could slightly speed up execution given how often sigmoid is used.
	 *
	 * @param x Function parameter
	 * @return approximate value of sigmoid(x)
	 */
	@Override
	public double f(double x) {
		return 1.0 / (1.0 + ActivationFunctions.quickExp(-x)); 
	}

	@Override
	public String name() {
		return "sigmoid(approx)";
	}
}
