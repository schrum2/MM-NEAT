package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent;

public class RawTetrisStateExtractor implements FeatureExtractor {

	@Override
	public int numFeatures() {
		return TetrisState.worldHeight * TetrisState.worldWidth;
	}

	@Override
	public double[] extract(Observation o) {
		TetrisState state = TetrisAfterStateAgent.observationToTetrisState(o);
		double[] result = new double[state.worldState.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Math.signum(state.worldState[i]);
		}
		return result;
	}

	@Override
	public String[] featureLabels() {
		String[] labels = new String[numFeatures()];
		int in = 0;
		for (int i = 0; i < TetrisState.worldHeight; i++) {
			for (int j = 0; j < TetrisState.worldWidth; j++) {
				labels[in++] = "(" + j + ", " + i + ") occupied?";
			}
		}
		return labels;
	}

	@Override
	public double[] scaleInputs(double[] inputs) {
		return inputs;
	}

}
