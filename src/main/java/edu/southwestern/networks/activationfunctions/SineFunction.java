package edu.southwestern.networks.activationfunctions;

public class SineFunction implements ActivationFunction {
	/**
	 * Sine function for x. Uses Math.sin();
	 *
	 * @param x Function parameter
	 * @return value of sin(x)
	 */
	@Override
	public double f(double x) {
		return Math.sin(x);
	}

	@Override
	public String name() {
		return "sin";
	}
}
