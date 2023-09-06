package edu.southwestern.networks.activationfunctions;

public class ReLUFunction implements ActivationFunction {
	/**
	 * ramp function, analogous to half-wave rectification in electrical engineering
	 * @param sum input
	 * @return result
	 */
	@Override
	public double f(double sum) {
		return Math.max(0, sum);
	}
	
	@Override
	public String name() {
		return "ReLU"; // "Rectified Linear Unit";
	}
}
