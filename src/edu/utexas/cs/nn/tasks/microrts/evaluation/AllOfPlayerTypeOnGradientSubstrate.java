package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.units.Unit;

public class AllOfPlayerTypeOnGradientSubstrate extends AllOfPlayerTypeSubstrate {

	BaseGradientSubstrate gradientSubstrate;
	double[][] gradient = null;
	
	// TODO: Also specify player?
	public AllOfPlayerTypeOnGradientSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers) {
		super(typesAndPlayers);
		//gradientSubstrate = new BaseGradientSubstrate(playerID);
	}
	
	/**
	 * Update gradient before getting inputs for unit locations
	 */
	@Override
	public double[][] getInputs(GameState gs) {
		gradient = gradientSubstrate.getInputs(gs);
		return super.getInputs(gs);
	}
	
	/**
	 * If unit is present, then its score depends on the gradient
	 */
	protected double scoreForUnit(Unit u) {
		return gradient[u.getX()][u.getY()];
	}

}
