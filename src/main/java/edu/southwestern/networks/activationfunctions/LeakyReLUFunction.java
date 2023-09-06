package edu.southwestern.networks.activationfunctions;

public class LeakyReLUFunction implements ActivationFunction {
	/**
	 * returns the leaky rectified function, which allows for a small, non-zero gradient when the unit is not active
	 * @param sum input
	 * @return result
	 */
	@Override
	public double f(double sum) {
		return (sum > 0) ? sum : 0.01 * sum;
	}

	@Override
	public String name() {
		return "ReLU(leaky)"; //"Leaky Rectified Linear Unit";
	}
}
