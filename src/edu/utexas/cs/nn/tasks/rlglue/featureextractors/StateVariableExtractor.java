package edu.utexas.cs.nn.tasks.rlglue.featureextractors;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

/**
 * Simple default feature extractor for RL-Glue that uses the raw state
 * variables.
 * 
 * @author Jacob Schrum
 */
public class StateVariableExtractor implements FeatureExtractor {

	private final TaskSpec tso;

	/**
	 * Sets the current StateVariableExtractor
	 */
	public StateVariableExtractor() {
		this(MMNEAT.tso);
	}

	/**
	 * Sets the current StateVariableExtractor
	 */
	public StateVariableExtractor(TaskSpec tso) {
		this.tso = tso;
	}

	/**
	 * Returns the number of features for the extractor
	 */
	@Override
	public int numFeatures() {
		return tso.getNumContinuousObsDims() + tso.getNumDiscreteObsDims();
	}

	/**
	 * Extracts feature information from the observation give
	 * @param o Observation
	 * @return double[] array of inputs
	 */
	@Override
	public double[] extract(Observation o) {
		double[] inputs = new double[o.intArray.length + o.doubleArray.length];
		for (int i = 0; i < o.intArray.length; i++) {
			inputs[i] = o.intArray[i];
		}
		for (int i = 0; i < o.doubleArray.length; i++) {
			inputs[o.intArray.length + i] = o.doubleArray[i];
		}

		return inputs;
	}

	/**
	 * Returns the feature labels for a given task spec
	 */
	@Override
	public String[] featureLabels() {
		int numDiscrete = tso.getNumDiscreteObsDims();
		int numContinuous = tso.getNumContinuousObsDims();
		String[] labels = new String[numDiscrete + numContinuous];
		int in = 0;
		for (int i = 0; i < numDiscrete; i++) {
			labels[in++] = "Discrete State Variable " + i;
		}
		for (int i = 0; i < numContinuous; i++) {
			labels[in++] = "Continuous State Variable " + i;
		}
		return labels;
	}

	/**
	 * Scales the given inputs, but this won't be needed for this extractor.
	 * @param double[] inputs
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		throw new UnsupportedOperationException(
				"There is no sensible way to scale inputs in general. More details are needed");
	}
}
