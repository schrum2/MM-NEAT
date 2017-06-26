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
	
	int substrateSize;
	private final double base_gradient_discount_rate = .95; 
	private final double lowest_allowed_brightness = .05;
	
	@Override
	public double[][] getInputs(GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		substrateSize = pgs.getHeight() * pgs.getWidth(); //assumes regular rectangle
		double[][] inputs = new double[pgs.getHeight() * numSubstrates][pgs.getWidth()];
		
		for(int i = 0; i < pgs.getHeight(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				Unit u = pgs.getUnitAt(j, i);
				if(u != null){
					if(u.getType().name.equals("Base") && u.getPlayer() == 0){
						activate(j,i,1,inputs);
					}
				}
			}
		}
		
		return inputs;
	}
	
	private void activate(int x, int y, double value, double[][] sub){
		if(value <= lowest_allowed_brightness) { //base case: trail too dim to matter.
			return;
		} else if(x >= sub.length || x <= 0 || y >= sub[0].length || y <= 0) { //base case: out of bounds
			return;
		} else {
			if(value > sub[x][y]) { //replace value only if value is > whats already there
				sub[x][y] = value;
				activate(x+1, y, value*(base_gradient_discount_rate), sub); //right
				activate(x-1, y, value*(base_gradient_discount_rate), sub); //left
				activate(x, y+1, value*(base_gradient_discount_rate), sub); //down
				activate(x, y-1, value*(base_gradient_discount_rate), sub); //up
			}
		}
	}
	
}
