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
	private int playerID;
	private double[][] inputs;
	private int numEnemyBuildings;

	public BaseGradientSubstrate(int player){
		playerID = player;
		numEnemyBuildings = 0;
	}

	@Override
	public double[][] getInputs(GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		baseGradientDiscountRate = (pgs.getWidth() / (double)(pgs.getWidth()+2)); //12: .85
		int previousNumEnemyBuildings = numEnemyBuildings;
		numEnemyBuildings = 0;
		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(((u != null && (u.getType().name.equals("Base") || u.getType().name.equals("Barracks")) && u.getPlayer() != playerID)))
					numEnemyBuildings++;
			}
		}
		if(previousNumEnemyBuildings != numEnemyBuildings){ //new building created, update inputs
			inputs = new double[pgs.getHeight()][pgs.getWidth()];
			for(int i = 0; i < pgs.getHeight(); i++){
				for(int j = 0; j < pgs.getWidth(); j++){
					Unit u = pgs.getUnitAt(j, i);
					if(u != null){
						if((u.getType().name.equals("Base") || u.getType().name.equals("Barracks")) && u.getPlayer() != playerID){
							activate(j,i,1,inputs);
						}
					}
				}
			}
		}

		return inputs;
	}

	private void activate(int x, int y, double value, double[][] sub){
		if(value <= LOWEST_ALLOWED_BRIGHTNESS) { //base case: trail too dim to matter.
			return;
		} else if(x >= sub.length || x < 0 || y >= sub[0].length || y < 0) { //base case: out of bounds
			return;
		} else {
			if(value > sub[x][y]) { //replace value only if value is > whats already there
				sub[x][y] = value;
				activate(x+1, y, value*(baseGradientDiscountRate), sub); //right
				activate(x-1, y, value*(baseGradientDiscountRate), sub); //left
				activate(x, y+1, value*(baseGradientDiscountRate), sub); //down
				activate(x, y-1, value*(baseGradientDiscountRate), sub); //up
			}
		}
	}

}
