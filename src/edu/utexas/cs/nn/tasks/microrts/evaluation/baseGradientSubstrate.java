package edu.utexas.cs.nn.tasks.microrts.evaluation;

import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

/**
 * substrate for microRTS that 
 * @author quintana
 *
 */
public class baseGradientSubstrate extends MicroRTSSubstrateInputs{
	
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
						
					}
				}
			}
		}
		
		return inputs;
	}
	
	private void activate(int location, double value, double[] sub, int width){
		if(value <= lowest_allowed_brightness) { //base case: trail too dim to matter.

		} else if(location < 0 || location > substrateSize - 1) { //base case: out of bounds
		
		} else {
			if(value > sub[location]) {//discontinue if value is < whats already there
				sub[location] = value;
				if ((location+1)/width != (location / width)) activate(location+1, value*(base_gradient_discount_rate), sub, width); //right
				if ((location-1)/width != (location / width)) activate(location-1, value*(base_gradient_discount_rate), sub, width); //left
				activate(location+width, value*(base_gradient_discount_rate), sub, width); //down
				activate(location-width, value*(base_gradient_discount_rate), sub, width); //up
			}
		}
	}
	
}
