package edu.southwestern.networks.activationfunctions;

import org.nd4j.linalg.activations.Activation;

import edu.southwestern.networks.ActivationFunctions;

public class SquareWaveFunction implements ActivationFunction {
	/**
	 * Generalization of square wave function with only one parameter.
	 * 
	 * @param x function parameter
	 * @return value of squareWave(x, 1, 1)
	 */
	@Override
	public double f(double x) {
		return ActivationFunctions.squareWave(x, 1, 1);
	}

	@Override
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "square"; //"Square Wave";
	}
}
