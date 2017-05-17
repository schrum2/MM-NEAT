package edu.utexas.cs.nn.tasks.microrts;

import micro.ai.evaluation.EvaluationFunction;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NNEvaluationFunction extends EvaluationFunction{
	//score modifier for these categories
	public static float RESOURCE_IN_BASE = 20; //UnitAction defines resource to not include unharvested resources
    public static float RESOURCE_IN_WORKER = 10;
    public static float UNIT_BONUS_MULTIPLIER = 40.0f;
    public static float BUILDING_MULTIPLIER = -1; //TODO use this somehow
    
	/**
	 * @param maxplayer - player to be evaluated
	 * @param minplayer - opponent
	 * @param gs - specified state of the game
	 * @return the amount of points that the player has more than the opponent 
	 * 			(0 is a tie and negative results mean evaluated player is losing)
	 */
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		return base_score(maxplayer,gs) - base_score(minplayer,gs);
	}
	
	/**
	 * 
	 * @param playerID 
	 * 				controller/agent being evaluated
	 * @param GameState gs
	 * 				specified state of the game 
	 * @return calculated score for this player at the specified game state
	 */
	public float base_score(int playerID, GameState gs){
		PhysicalGameState pgs = gs.getPhysicalGameState();
		float score = gs.getPlayer(playerID).getResources()*RESOURCE_IN_BASE;
		for(Unit u:pgs.getUnits()) {
            if (u.getPlayer()==playerID) {
                score += u.getResources() * RESOURCE_IN_WORKER;
                score += UNIT_BONUS_MULTIPLIER * (u.getCost()*u.getHitPoints())/(float)u.getMaxHitPoints(); //may need to change this to give more of a gradient
            }
        }
        return score;
	}

	@Override
	public float upperBound(GameState gs) {
		return 0;
	}

}
