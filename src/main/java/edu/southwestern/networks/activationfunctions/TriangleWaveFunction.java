package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

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
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}
	
	@Override
	public String name() {
		return "triangle"; //"Triangle Wave";
	}
}
