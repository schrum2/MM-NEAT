package edu.southwestern.networks.activationfunctions;

public class TanHFunction implements ActivationFunction {

	/**
	 * Function for tanh, hyperbolic tangent. Uses Math.tanh, no approximation.
	 *
	 * @param x Function parameter
	 * @return value of tanh(x)
	 */
	@Override
	public double f(double x) {
		return Math.tanh(x);
	}

	@Override
	public String name() {
		return "tanh";
	}
}
