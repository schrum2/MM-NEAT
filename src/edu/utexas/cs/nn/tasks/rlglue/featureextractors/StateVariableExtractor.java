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

	public StateVariableExtractor() {
		this(MMNEAT.tso);
	}

	public StateVariableExtractor(TaskSpec tso) {
		this.tso = tso;
	}

	@Override
	public int numFeatures() {
		return tso.getNumContinuousObsDims() + tso.getNumDiscreteObsDims();
	}

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

	@Override
	public double[] scaleInputs(double[] inputs) {
		throw new UnsupportedOperationException(
				"There is no sensible way to scale inputs in general. More details are needed");
	}
}
