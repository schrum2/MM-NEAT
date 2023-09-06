package edu.southwestern.networks.activationfunctions;

public class FullGaussianFunction implements ActivationFunction {
	/**
	 * Gaussian stretched to the range [-1,1].
	 * This is the version used in the original Picbreeder.
	 * @param x parameter
	 * @return result
	 */
	@Override
	public double f(double x) {
		return Math.exp(-x*x)*2 - 1;
	}

	@Override
	public String name() {
		return "gauss-full"; //"Full Gaussian";
	}
}
