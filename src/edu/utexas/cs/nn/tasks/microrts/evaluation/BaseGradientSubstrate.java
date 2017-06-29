package edu.utexas.cs.nn.tasks.microrts.evaluation;

import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

/**
 * substrate for microRTS that 
 * @author quintana
 *
 */
public class BaseGradientSubstrate extends MicroRTSSubstrateInputs{

	private double baseGradientDiscountRate; 
	private static final double LOWEST_ALLOWED_BRIGHTNESS = .05;
	// Assumes 2 players ... to restrictive?
	private double[][][] inputs = new double[2][][]; // One gradient from perspective of each player [player][x][y]
	private int numBuildings;
	private boolean trackEnemyBuildings;

	public BaseGradientSubstrate(boolean enemy){
		trackEnemyBuildings = enemy;
		numBuildings = 0;
	}

	@Override
	public double[][] getInputs(GameState gs, int playerToEvaluate) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		baseGradientDiscountRate = (pgs.getWidth() / (double)(pgs.getWidth()+2)); //12: .85
		int previousNumBuildings = numBuildings;
		numBuildings = 0;
		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(trackBuilding(u,playerToEvaluate))
					numBuildings++;
			}
		}
		if(previousNumBuildings != numBuildings){ //new building created, update inputs
			inputs[playerToEvaluate] = new double[pgs.getHeight()][pgs.getWidth()];
			for(int i = 0; i < pgs.getHeight(); i++){
				for(int j = 0; j < pgs.getWidth(); j++){
					Unit u = pgs.getUnitAt(j, i);
					if(trackBuilding(u,playerToEvaluate)){
						activate(j,i,1,inputs[playerToEvaluate],gs);
					}
				}
			}
		}

		return inputs[playerToEvaluate];
	}
	
	/**
	 * Whether this unit is a building to be tracked
	 * @param u
	 * @param playerToEvaluate
	 * @return
	 */
	private boolean trackBuilding(Unit u, int playerToEvaluate) {
		return ( u != null // not null
				&& (u.getType().name.equals("Base") || u.getType().name.equals("Barracks")) // is a building
				&& (       (trackEnemyBuildings && u.getPlayer() != playerToEvaluate)    // building belongs to enemy, so track it 
						|| (!trackEnemyBuildings && u.getPlayer() == playerToEvaluate) ) ); // building belongs to me, so track it  
	}

	private void activate(int x, int y, double value, double[][] sub, GameState gs){
		if(value <= LOWEST_ALLOWED_BRIGHTNESS) { //base case: trail too dim to matter.
			return;
		} else if(x >= sub.length || x < 0 || y >= sub[0].length || y < 0) { //base case: out of bounds
			return;
		} else if(gs.getPhysicalGameState().getTerrain(x, y)!=PhysicalGameState.TERRAIN_NONE) { //base case: impassable terrain
			return;
		} else {
			if(value > sub[x][y]) { //replace value only if value is > whats already there
				sub[x][y] = value;
				activate(x+1, y, value*(baseGradientDiscountRate), sub, gs); //right
				activate(x-1, y, value*(baseGradientDiscountRate), sub, gs); //left
				activate(x, y+1, value*(baseGradientDiscountRate), sub, gs); //down
				activate(x, y-1, value*(baseGradientDiscountRate), sub, gs); //up
			}
		}
	}

}
