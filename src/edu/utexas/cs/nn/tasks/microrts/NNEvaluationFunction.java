package edu.utexas.cs.nn.tasks.microrts;

import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;

public class NNEvaluationFunction extends EvaluationFunction{

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		return 0;
	}

	@Override
	public float upperBound(GameState gs) {
		return 0;
	}

}
