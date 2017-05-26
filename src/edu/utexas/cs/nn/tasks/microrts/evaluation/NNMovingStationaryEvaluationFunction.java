package edu.utexas.cs.nn.tasks.microrts.evaluation;

import micro.rts.GameState;
import micro.rts.units.Unit;

/**
 * 
 * @author alicequint
 * distinguishes between mobile and immobile units and puts them on their own substrates
 */
public class NNMovingStationaryEvaluationFunction extends NNEvaluationFunction{
	
	private int substrateSize;

	@Override
	protected double[] gameStateToArray(GameState gs) {
		substrateSize = pgs.getWidth()*pgs.getHeight();
		double[] inputs = new double[substrateSize * 2];
		Unit current = null;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				current = pgs.getUnitAt(i, j);
				if(current!= null){
					if(current.getType().canMove){
						inputs[i*pgs.getWidth() + j] = current.getID(); 
					} else {
						inputs[i*pgs.getWidth() + j + substrateSize] = current.getID();
					}
				}
			}
		}
		return inputs;
	}

	@Override
	public String[] sensorLabels() {
		String[] labels = new String[substrateSize * 2];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				labels[i*pgs.getWidth() + j] = "Mobile unit:  (" + i + ", " + j + ")";
				labels[i*pgs.getWidth() + j + substrateSize] = "Immobile unit:  (" + i + "," + j + ")";
			}
		}
		return labels;
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
