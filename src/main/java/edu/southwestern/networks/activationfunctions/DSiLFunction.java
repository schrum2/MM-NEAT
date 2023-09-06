package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class DSiLFunction implements ActivationFunction {

	/**
	 * Returns the derivative of the sigmoid weighted linear unit of the input.
	 * dSiL(x) = SiL'(x) = SiL(x)/dx = sigmoid(x) * (1 + x*(1 - sigmoid(x))) See
	 * https://arxiv.org/pdf/1702.03118.pdf
	 */
	@Override
	public double f(double x) {
		double sig = ActivationFunctions.sigmoid(x);
		return sig * (1 + x * (1 - sig));
	}

	@Override
	public String name() {
		return "dSiL";
	}

}
