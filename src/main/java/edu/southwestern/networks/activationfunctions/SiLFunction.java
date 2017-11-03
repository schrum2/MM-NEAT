package edu.southwestern.networks.activationfunctions;
import org.nd4j.linalg.activations.Activation;

public class SiLFunction implements ActivationFunction {

	/**
	 * Returns the sigmoid weighted linear unit of the input.
	 * See https://arxiv.org/pdf/1702.03118.pdf
	 */
	@Override
	public double f(double x) {
		return x * ActivationFunction.sigmoid(x);
	}

	@Override
	public Activation equivalentDL4JFunction() {
		throw new UnsupportedOperationException("No corresponding DL4J function for " + name());
	}

	@Override
	public String name() {
		return "SiL";
	}

}
