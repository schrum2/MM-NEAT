package edu.southwestern.networks.activationfunctions;

public class ClippedAbsValFunction extends HalfLinearPiecewiseFunction {
	/**
	 * Absolute value function for x. Uses Math.abs();
	 * Also clamps result to range [0,1] after use of absolute value because of
	 * problems with values rising to infinity.
	 *
	 * @param x Function parameter
	 * @return value of abs(x) clamped to [0,1]
	 */
	@Override
	public double f(double x) {
		return super.f(Math.abs(x));
	}
	
	@Override
	public String name() {
		return "abs"; //"Absolute Value";
	}
}
