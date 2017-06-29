package edu.utexas.cs.nn.tasks.microrts.evaluation.substrates;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.units.Unit;

public class AllOfPlayerTypeOnGradientSubstrate extends AllOfPlayerTypeSubstrate {

	BaseGradientSubstrate gradientSubstrate;
	double[][] gradient = null;
	
	public AllOfPlayerTypeOnGradientSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers, boolean trackEnemyBuildings) {
		super(typesAndPlayers);
		gradientSubstrate = new BaseGradientSubstrate(trackEnemyBuildings);
	}
	
	/**
	 * Update gradient before getting inputs for unit locations
	 */
	@Override
	public double[][] getInputs(GameState gs, int playerToEvaluate) {
		gradient = gradientSubstrate.getInputs(gs,playerToEvaluate);
		return super.getInputs(gs,playerToEvaluate);
	}
	
	/**
	 * If unit is present, then its score depends on the gradient
	 */
	protected double scoreForUnit(Unit u) {
		return gradient[u.getX()][u.getY()];
	}

}
