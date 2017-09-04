package edu.southwestern.tasks.rlglue.featureextractors.cartpole;

import edu.southwestern.tasks.rlglue.featureextractors.FeatureExtractor;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Extractor for learning pole balancing without the derivative information
 * 
 * @author Jacob Schrum
 */
public class NonMarkovianCartPoleExtractor implements FeatureExtractor {

	/**
	 * Cart position and pole angle (no derivatives)
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
         * Actually still need to implement this.
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		// TODO Auto-generated method stub
		return null;
	}
}
