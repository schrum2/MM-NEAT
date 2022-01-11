package edu.southwestern.tasks.rlglue.featureextractors;

import org.rlcommunity.rlglue.codec.taskspec.TaskSpec;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.southwestern.tasks.rlglue.RLGlueTask;

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
		this(RLGlueTask.tso);
	}

	/**
	 * Sets the current StateVariableExtractor
         * @param tso Task specification
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
                System.arraycopy(o.doubleArray, 0, inputs, o.intArray.length, o.doubleArray.length);
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
	 */
	@Override
	public double[] scaleInputs(double[] inputs) {
		throw new UnsupportedOperationException("There is no sensible way to scale inputs in general. More details are needed");
	}
}
