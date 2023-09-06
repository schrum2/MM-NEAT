package edu.southwestern.networks.activationfunctions;

public class FullLinearPiecewiseFunction implements ActivationFunction {

	/**
	 * Linear function that returns x within the bounds of -1 < x < 1
	 *
	 * @param x Function parameter
	 * @return linear x within -1 and 1
	 */
	@Override
	public double f(double x) {
		return fullLinear(x);
	}
	
	/**
	 * Static version of function allowing easy access in various functions
	 * @param x
	 * @return
	 */
	public static double fullLinear(double x) {
		return Math.max(-1, Math.min(1, x));
	}
	
	@Override
	public String name() {
		return "piecewise-full"; //"Full Piecewise";
	}
}
