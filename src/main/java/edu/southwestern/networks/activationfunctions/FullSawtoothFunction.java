package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class FullSawtoothFunction implements ActivationFunction {
	/**
	 * Generalization of fullSawtooth with only one parameter.
	 * @param x Input parameter
	 * @return value of fullSawtooth(x, 1)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.fullSawtooth(x,1);
	}

	@Override
	public String name() {
		return "sawtooth-full";
	}
}
