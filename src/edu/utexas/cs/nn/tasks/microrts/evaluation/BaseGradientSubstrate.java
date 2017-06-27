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
	
	private static final double BASE_GRADIENT_DISCOUNT_RATE = .7; 
	private static final double LOWEST_ALLOWED_BRIGHTNESS = .05;
	private int playerID;
	
	public BaseGradientSubstrate(int player){
		playerID = player;
	}
	
	@Override
	public double[][] getInputs(GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[][] inputs = new double[pgs.getHeight()][pgs.getWidth()];
		
		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(u != null){
					if(u.getType().name.equals("Base") && u.getPlayer() != playerID){
						activate(j,i,1,inputs);
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
				activate(x+1, y, value*(BASE_GRADIENT_DISCOUNT_RATE), sub); //right
				activate(x-1, y, value*(BASE_GRADIENT_DISCOUNT_RATE), sub); //left
				activate(x, y+1, value*(BASE_GRADIENT_DISCOUNT_RATE), sub); //down
				activate(x, y-1, value*(BASE_GRADIENT_DISCOUNT_RATE), sub); //up
			}
		}
	}
	
}
