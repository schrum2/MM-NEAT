package edu.southwestern.networks.activationfunctions;

public class TriangleWaveFunction extends FullSawtoothFunction {
	/**
	 * Triangle wave can be represented as the absolute value of the sawtooth function.
	 * Piecewise linear, continuous real function - useful for sound generation in Java
	 * 
	 * @param x function parameter
	 * @param a period 
	 * @return value of fullSawtooth(x, a)
	 */
	public double f(double x) {
		return Math.abs(super.f(x));
	}
		
	@Override
	public String name() {
		return "triangle"; //"Triangle Wave";
	}
}
