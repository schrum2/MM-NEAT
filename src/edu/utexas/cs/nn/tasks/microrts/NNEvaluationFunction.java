package edu.utexas.cs.nn.tasks.microrts;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NNEvaluationFunction<T extends Network> extends EvaluationFunction{
	
	Network nn;
	//score modifier for these categories
	public static float RESOURCE_IN_BASE = 20; //UnitAction defines resource to not include unharvested resources
    public static float RESOURCE_IN_WORKER = 10;
    public static float UNIT_BONUS_MULTIPLIER = 40.0f;
    public static float BUILDING_MULTIPLIER = -1;
    
    public NNEvaluationFunction(Genotype<T> g){
    	nn = g.getPhenotype();
    }
    
	/**
	 * @param maxplayer - player to be evaluated
	 * @param minplayer - opponent
	 * @param gs - specified state of the game
	 * @return 
	 */
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		return -1f;
	}
	
	private double[] gameStateToArray(GameState gs){
		double[] board = new double[0];
		return null;
	}

	/**
	 * maximum possible thing returned by the evaluation function
	 */
	@Override
	public float upperBound(GameState gs) {
        return 1f;
	}
}
