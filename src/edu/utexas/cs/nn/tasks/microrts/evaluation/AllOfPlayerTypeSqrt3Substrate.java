package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.ai.evaluation.SimpleSqrtEvaluationFunction3;
import micro.rts.units.Unit;

public class AllOfPlayerTypeSqrt3Substrate extends AllOfPlayerTypeSubstrate {

	// TODO: Also specify player?
	public AllOfPlayerTypeSqrt3Substrate(ArrayList<Pair<String, Integer>> typesAndPlayers) {
		super(typesAndPlayers);
	}
	
	protected double scoreForUnit(Unit u) {
		return (u.getResources() * SimpleSqrtEvaluationFunction3.RESOURCE_IN_WORKER) + (SimpleSqrtEvaluationFunction3.UNIT_BONUS_MULTIPLIER * u.getCost()*Math.sqrt( u.getHitPoints()/u.getMaxHitPoints() ));
	}

}
