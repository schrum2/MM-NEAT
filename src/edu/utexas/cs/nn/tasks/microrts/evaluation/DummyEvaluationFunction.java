package edu.utexas.cs.nn.tasks.microrts.evaluation;

import micro.rts.GameState;

public class DummyEvaluationFunction extends NNEvaluationFunction{

	//not used
	@Override
	protected double[] gameStateToArray(GameState gs) {
		return new double[0];
	}

	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
