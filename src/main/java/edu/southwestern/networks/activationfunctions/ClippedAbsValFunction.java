package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

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
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "abs"; //"Absolute Value";
	}
}
