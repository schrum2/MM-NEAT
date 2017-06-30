package edu.utexas.cs.nn.tasks.microrts.evaluation.substrates;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.ai.evaluation.SimpleSqrtEvaluationFunction3;
import micro.rts.units.Unit;

public class AllOfPlayerTypeSqrt3Substrate extends AllOfPlayerTypeSubstrate {

	public AllOfPlayerTypeSqrt3Substrate(ArrayList<Pair<String, Integer>> typesAndPlayers) {
		super(typesAndPlayers);
	}
	
	protected double scoreForUnit(Unit u) {
		System.out.println("# " + u.getHitPoints()+ " " + u.getMaxHitPoints() + (double)Math.sqrt((double)u.getHitPoints()/(double)u.getMaxHitPoints() ));
		double d = (u.getResources() * SimpleSqrtEvaluationFunction3.RESOURCE_IN_WORKER) + 
				(SimpleSqrtEvaluationFunction3.UNIT_BONUS_MULTIPLIER * u.getCost()*Math.sqrt(u.getHitPoints()/(double)u.getMaxHitPoints()));
		System.out.println("assigning value to " + u.getType().name + " : " + d);
		return d; 
	}

}
