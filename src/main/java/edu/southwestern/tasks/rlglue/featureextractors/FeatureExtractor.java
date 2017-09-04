package edu.southwestern.tasks.rlglue.featureextractors;

import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * RL-Glue domains use a feature extractor that takes a raw Observation and
 * turns it into an array of doubles which can be fed to a neural network or
 * other policy.
 * 
 * @author Jacob Schrum
 */
public interface FeatureExtractor {

	/**
	 * Number of features extracted. Specifies length of array returned by
	 * extract below.
	 * 
	 * @return length of feature array
	 */
	public int numFeatures();

	/**
	 * Takes a raw state Observation and returns a human-specified vector of
	 * relevant features. Features should be raw, human-readable numbers. If
	 * values need to be scaled for use by the policy, then use the scaleInputs
	 * method below.
	 * 
	 * @param o
	 *            State Observation from RL Glue
	 * @return unscaled feature vector
	 */
	public double[] extract(Observation o);

	/**
	 * String labels associated with each feature. The length of the array
	 * should match the number of features.
	 * 
	 * @return array of feature names
	 */
	public String[] featureLabels();

	/**
	 * Scale raw feature values into inputs for a policy like a neural network.
	 * This generally means normalizing all features to the range [0,1]
	 * 
	 * @param inputs
	 *            Original, unscaled feature values
	 * @return scaled/normalized features
	 */
	public double[] scaleInputs(double[] inputs);
}
