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
					if(isAllowedInSub(u)){
						inputs[j][i] = 1;
					}
					if(terrain && pgs.getTerrain(j, i) == pgs.TERRAIN_WALL){
						inputs[j][i] = 1;
					}
				}
			}
		}
		return inputs;
	}

	private boolean isAllowedInSub(Unit u) {
		boolean isAllowed = true;
		String key;
		int player;
		for(Pair<String, Integer> criteria : typesAndPlayers){
			key = criteria.t1;
			player = criteria.t2;
			if(key != null && !u.getType().name.equals("key"))
				if (key.equals("mobile") && !u.getType().canMove){
					isAllowed = false;
				} else if (key.equals("immobile") && u.getType().canMove){
					isAllowed = false;
				} //else unit fits key, is allowed.
			if(player != ANY_PLAYER && u.getPlayer() != player)
				isAllowed = false;
			
			if(isAllowed) //unit meets at least 1 criteria, no need to search the rest.
				return isAllowed;
		}
		return isAllowed;
	}

}
