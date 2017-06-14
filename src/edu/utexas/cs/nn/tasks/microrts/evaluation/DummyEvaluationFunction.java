package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;

/**
 * evaluation function to be used with microRTSAgents that is purposefully
 * bad so that we can see if the evaluation function affects results
 * 
 * @author alicequint
 *
 * @param <T>
 */
public class DummyEvaluationFunction<T extends Network>  extends NNEvaluationFunction<T>{

	//constructor
	public DummyEvaluationFunction(){	
	}
	
	//not used
	@Override
	protected double[] gameStateToArray(GameState gs) {
		return new double[0];
	}

	@Override
	public String[] sensorLabels() {
		return new String[0];
	}
	
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		return 0;
	}
	
}
