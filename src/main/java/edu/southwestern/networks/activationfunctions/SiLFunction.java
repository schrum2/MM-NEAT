package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class SiLFunction implements ActivationFunction {

	/**
	 * Returns the sigmoid weighted linear unit of the input.
	 * See https://arxiv.org/pdf/1702.03118.pdf
	 */
	@Override
	public double f(double x) {
		return x * ActivationFunctions.sigmoid(x);
	}

	@Override
	public String name() {
		return "SiL";
	}

}
