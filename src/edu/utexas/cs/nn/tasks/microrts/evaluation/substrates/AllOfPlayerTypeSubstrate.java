package edu.utexas.cs.nn.tasks.microrts.evaluation.substrates;

import java.util.ArrayList;

import edu.utexas.cs.nn.util.datastructures.Pair;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class AllOfPlayerTypeSubstrate extends MicroRTSSubstrateInputs{

	private boolean terrain;
	private ArrayList<Pair<String, Integer>> typesAndPlayers;
	public final static int ANY_PLAYER = -2;
	public final static int RESOURCE = -1;
	public final static int MY_PLAYER = 0;
	public final static int ENEMY_PLAYER = 1;
	
	/**
	 * 
	 * @param typesAndPlayers
	 * 				description of all the categories of units that are allowed into the substrate
	 */
	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers){
		this(typesAndPlayers, false);
	}

	public AllOfPlayerTypeSubstrate(ArrayList<Pair<String, Integer>> typesAndPlayers, boolean terrain){
		this.typesAndPlayers = typesAndPlayers;
		this.terrain = terrain;
	}

	@Override
	public double[][] getInputs(GameState gs, int evaluatedPlayer) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[][] inputs = new double[pgs.getHeight()][pgs.getWidth()];

		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(u != null){
					inputs[j][i] = valueInSub(u, evaluatedPlayer);
				} else if(terrain && pgs.getTerrain(j, i) == PhysicalGameState.TERRAIN_WALL){
					inputs[j][i] = 1;
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
	
	protected double valueInSub(Unit u, int evaluatedPlayer) {
		// Sign of result depends on whether the unit is of the evaluating player or the enemy
		String key;
		int player;
		
		for(Pair<String, Integer> criteria : typesAndPlayers){
			key = criteria.t1;
			player = criteria.t2;
			if( (key == null && player == RESOURCE     && RESOURCE == u.getPlayer()) // Resources
			||  (key == null && player == ANY_PLAYER   && RESOURCE != u.getPlayer()) // All units and players
			||  (key == null && player == MY_PLAYER    && evaluatedPlayer == u.getPlayer()) // Any unit of the my player		
			||  (key == null && player == ENEMY_PLAYER && RESOURCE != u.getPlayer() && evaluatedPlayer == ((u.getPlayer()+1)%2)) // Any unit of the enemy player		
		    ||  (u.getType().name.equals(key) && player == ANY_PLAYER) // Specific unit of either player
			||  (u.getType().name.equals(key) && player == MY_PLAYER    && evaluatedPlayer == u.getPlayer()) // Specific unit of my player
		    ||  (u.getType().name.equals(key) && player == ENEMY_PLAYER && RESOURCE != u.getPlayer() && evaluatedPlayer == ((u.getPlayer()+1)%2)) // Specific unit of enemy player
		    ){ 
				return (evaluatedPlayer == u.getPlayer() ? 1 : -1) * scoreForUnit(u);
			} 			
		}
		return 0; // 0 if it makes it here
	}

	public String toString() {
		return this.getClass().getSimpleName() + ":" + typesAndPlayers;
	}
}
