package edu.southwestern.networks.activationfunctions;

import edu.southwestern.networks.ActivationFunctions;

public class SawtoothFunction implements ActivationFunction {

	/**
	 * Sawtooth function for x. mimics sine but in piecewise way. Uses
	 * Math.floor().
	 *
	 * @param x Function parameter
	 * @return value of sawtooth(x)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.halfSawtooth(x);
	}

	@Override
	public String name() {
		return "sawtooth";
	}
}
