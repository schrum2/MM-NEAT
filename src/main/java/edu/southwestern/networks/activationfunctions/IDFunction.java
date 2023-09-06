package edu.southwestern.networks.activationfunctions;

public class IDFunction implements ActivationFunction {

	/**
	 * Returns input value
	 */
	@Override
	public double f(double x) {
		return x;
	}

	@Override
	public String name() {
		return "id";
	}
}
