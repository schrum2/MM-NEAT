package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class AllOfPlayerTypeSubstrate extends MicroRTSSubstrateInputs{

	private boolean terrain;
	private ArrayList<Pair<String, Integer>> typesAndPlayersAllowed;
	final static int ANY_PLAYER = -2;
	
	/**
	 * 
	 * @param typesAndPlayers
	 * 				description of all the categories of units that are allowed into the substrate
	 */
	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers){
		this.typesAndPlayersAllowed = typesAndPlayers;
		terrain = false;
	}
	
	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers, boolean terrain){
		this.typesAndPlayersAllowed = typesAndPlayers;
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
					if(terrain && pgs.getTerrain(j, i) == pgs.TERRAIN_WALL){
						inputs[j][i] = 1;
					}
				}
			}
		}
		return inputs;
	}

	/**
	 * decides whether a given unit should be in the substrate
	 * 
	 * @param u
	 * 		unit to be valued (precondition: u isnt null) 
	 * @return
	 * 		value of unit in sub (can be 0 if it doesnt fit criteria)
	 */
	private double valueInSub(Unit u) {
		double valueIfMatch = 1.0;
		boolean matchKey = false;
		boolean matchPlayer = false;
		String key;
		int player;
		for(Pair<String, Integer> criteria : typesAndPlayersAllowed){
			key = criteria.t1;
			player = criteria.t2;
			if(key == null || (((u.getType().name.equals(key) || (u.getType().canMove && key.equals("mobile")) || (!u.getType().canMove && key.equals("immobile")))))){
				matchKey = true;
			}
			if(matchKey && (player == u.getPlayer() || player == ANY_PLAYER)){ //only checks player if it passes the name criteria
				matchPlayer = true;
				if(player == ANY_PLAYER && u.getPlayer() == 1){
					valueIfMatch = -1;
				}
			}
			if(matchKey && matchPlayer)
				return valueIfMatch;
		} //end for
		//didnt match any of the criteria
		return 0;
	}

}
