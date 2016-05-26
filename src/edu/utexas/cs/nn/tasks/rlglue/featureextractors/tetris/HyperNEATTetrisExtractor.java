package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import org.rlcommunity.environments.tetris.TetrisState;
import org.rlcommunity.rlglue.codec.types.Observation;

import edu.utexas.cs.nn.tasks.rlglue.featureextractors.FeatureExtractor;
import edu.utexas.cs.nn.tasks.rlglue.tetris.TetrisAfterStateAgent;

public class HyperNEATTetrisExtractor implements FeatureExtractor {

	@Override
	public int numFeatures() {
		return TetrisState.worldHeight*TetrisState.worldWidth-1;//is -1 right?
	}

	@Override
	public double[] extract(Observation o) {
		TetrisState state = TetrisAfterStateAgent.observationToTetrisState(o);
		int[] worldState = state.worldState;
		double[] result = new double[worldState.length];
		for(int i = 0; i < result.length; i++) {
			result[i] = Math.signum(worldState[i]);
		}
		return result;
	}

	@Override
	public String[] featureLabels() {
		 String[] labels = new String[numFeatures()];
	        int in = 0;
	        for (int i = 0; i < TetrisState.worldWidth; i++) {
	        for (int j = 0; j < TetrisState.worldWidth - 1; j++) {
	            labels[in++] = "Coordinates: (" + i + ", " + j + ")";
	        	}
	        }
	        return labels;
	}

	@Override
	public double[] scaleInputs(double[] inputs) {
		return inputs;
	}

}
