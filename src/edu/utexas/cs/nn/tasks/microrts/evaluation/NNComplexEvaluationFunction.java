package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;

/**
 * @author alicequint
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction{

	
	//only does terrain atm
	private double[] gameStateToArray(GameState gs) {
		pgs = gs.getPhysicalGameState();
		double[] basicState = new double[pgs.getHeight()*pgs.getWidth()];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getWidth(); j++){
				if(pgs.getTerrain(i, j) == pgs.TERRAIN_WALL)
					basicState[i*pgs.getWidth() + j] = -1;
			}
		}
		return basicState;
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
