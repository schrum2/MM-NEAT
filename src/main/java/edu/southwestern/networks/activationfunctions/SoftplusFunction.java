package edu.southwestern.networks.activationfunctions;

public class SoftplusFunction implements ActivationFunction {
	/**
	 * The smooth approximation of the reLU function
	 * @param sum
	 * @return
	 */
	@Override
	public double f(double sum) {
		return Math.log(1 + Math.pow(Math.E, sum));
	}

	@Override
	public String name() {
		return "softplus";
	}
}
