package edu.utexas.cs.nn.tasks.rlglue.featureextractors.cartpole;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 *
 * @author Jacob Schrum
 */
public class NonMarkovianCartPoleExtractor implements FeatureExtractor {

	/**
	 * Returns the number of featuers for this extractor
	 */
	@Override
	public int numFeatures() {
		return 2;
	}
	/**
	 * Extracts feature information from the observation give
	 * @param o Observation
	 * @return double[] array of inputs
	 */
	@Override
	public double[] extract(Observation o) {
		// Only use x and theta, and not the derivatives
		return new double[] { o.doubleArray[0], o.doubleArray[2] };
	}

	/**
	 * Returns the feature labels for this extractor
	 */
	@Override
	public String[] featureLabels() {
		return new String[] { "Position", "Angle" };
	}

	/**
	 * Scales the given inputs, not implemented here.
	 * @param double[] inputs
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}
}
