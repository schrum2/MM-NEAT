package edu.southwestern.networks.activationfunctions;

public class FullQuickSigmoidFunction extends QuickSigmoidFunction {

	/**
	 * Quick approximation to sigmoid within the bounds of -1 < x < 1.
	 * Inaccurate, but has needed properties. Could slightly speed up execution
	 * given how often sigmoid is used. 
	 * @param x Function parameter 
	 * @return sigmoid approximation within -1 and 1
	 */
	@Override
	public double f(double x) {
		return (2 * super.f(x)) - 1;
	}
	
	@Override
	public String name() {
		return "sigmoid(approx)-full"; //"Full Approximate Sigmoid";
	}
}
