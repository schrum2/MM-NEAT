package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NN2DEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	public static final double BASE_WEIGHT = 4; //hard to quantify because different amount of importance at different stages of the game
	public static final double BASE_RESOURCE_WEIGHT = .25;
	public static final double BARRACKS_WEIGHT = 2.5;
	public static final double WORKER_WEIGHT = 1;
	public static final double WORKER_RESOURCE_WEIGHT = .15;
	//these subject to change because in experiments so far there have rarely been multiple non-worker units
	public static final double LIGHT_WEIGHT = 3; 
	public static final double HEAVY_WEIGHT = 3.25;
	public static final double RANGED_WEIGHT = 3.75;
	public static final double RAW_RESOURCE_WEIGHT = .01;
	
	/**
	 * represents all squares of the gameState in an array
	 */
	protected double[] gameStateToArray(GameState gs) {
		pgs = gs.getPhysicalGameState();
		double[] board = new double[pgs.getHeight()*pgs.getWidth()];
		int boardIndex;
		Unit currentUnit;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				boardIndex = i * pgs.getWidth() + j;
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					switch(currentUnit.getType().name){
					case "Worker": board[boardIndex] = WORKER_WEIGHT + (WORKER_RESOURCE_WEIGHT * currentUnit.getResources()); break; 
					case "Light": board[boardIndex] = LIGHT_WEIGHT; break;
					case "Heavy": board[boardIndex] = HEAVY_WEIGHT; break;
					case "Ranged": board[boardIndex] = RANGED_WEIGHT; break;
					case "Base": board[boardIndex] = BASE_WEIGHT + (BASE_RESOURCE_WEIGHT * currentUnit.getResources()); break;
					case "Barracks": board[boardIndex] = BARRACKS_WEIGHT; break;
					case "Resource": board[boardIndex] = RAW_RESOURCE_WEIGHT; break;
					default: break;
					}
					if(currentUnit.getPlayer() == 1) board[boardIndex] *= -1; 
				}
			}//end inner loop
		}//end outer loop
		return board;
	}

	@Override
	public String[] sensorLabels() {
		assert pgs != null : "There must be a physical game state in order to extract height and width";
		String[]labels = new String[pgs.getHeight()*pgs.getWidth()];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				String label = "unit at (" + i + ", " + j + ")";
				labels[i*pgs.getWidth() + j] = label;
			} 
		}
		return labels; 
	}
	
	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		double[] inputs = gameStateToArray(gs);
		double[] outputs = nn.process(inputs);
		float score = (float) outputs[0];
		return score;
	}

}
