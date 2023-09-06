package edu.southwestern.networks.activationfunctions;

public class CosineFunction implements ActivationFunction {

	/**
	 * Cosine: didn't have this originally, since sine
	 * seemed good enough, but apparently the original
	 * Picbreeder has cosine in it.
	 * @param x parameter
	 * @return result
	 */
	@Override
	public double f(double x) {
		return Math.cos(x);
	}

	@Override
	public String name() {
		return "cos"; //"Cosine";
	}

}
