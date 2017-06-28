package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class AllOfPlayerTypeSubstrate extends MicroRTSSubstrateInputs{

	private boolean terrain;
	private ArrayList<Pair<String, Integer>> typesAndPlayers;
	final static int ANY_PLAYER = -2;
	
	/**
	 * 
	 * @param typesAndPlayers
	 * 				description of all the categories of units that are allowed into the substrate
	 */
	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers){
		this.typesAndPlayers = typesAndPlayers;
		terrain = false;
	}
	
	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers, boolean terrain){
		this.typesAndPlayers = typesAndPlayers;
		this.terrain = terrain;
	}

	@Override
	public double[][] getInputs(GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[][] inputs = new double[pgs.getHeight()][pgs.getWidth()];

		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(u != null){
					inputs[j][i] = valueInSub(u);
					if(terrain && pgs.getTerrain(j, i) == PhysicalGameState.TERRAIN_WALL){
						inputs[j][i] = scoreForUnit(u);
					}
				}
			}
		}
		return inputs;
	}
	
	/**
	 * Different types of units can have different scores associated with them
	 * @param u Unit to score
	 * @return Score for that unit on the substrate
	 */
	protected double scoreForUnit(Unit u) {
		return 1.0; // default
	}

	private double valueInSub(Unit u) {
		double value = 0;
		String key;
		int player;
		for(Pair<String, Integer> criteria : typesAndPlayers){
			key = criteria.t1;
			player = criteria.t2;
			if(key != null && !u.getType().name.equals("key")) //doesnt fit key directly
				if (key.equals("mobile") && !u.getType().canMove){ //if contradicts special key: mobile
					return value;
				} else if (key.equals("immobile") && u.getType().canMove){ //if contradicts special key: immobile
					return value;
				} //else unit fits key; does it fit player?
			if(player == ANY_PLAYER && u.getPlayer() != -1){
				value = u.getPlayer() == player ? 1 : -1;
				return value;
			}
			else if(u.getPlayer() != player){
				return value;
			}
		}
		return value; // 0 if it makes it here
	}

}
