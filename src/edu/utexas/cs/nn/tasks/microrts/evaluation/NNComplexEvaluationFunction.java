package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;

/**
 * @author alicequint
 */
public class NNComplexEvaluationFunction<T extends Network> extends NNEvaluationFunction{

	@Override
	//should not be called externally, only public because of parent class
	public double[] gameStateToArray(GameState gs) {
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
		return new String[]{""};
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		return 0;
	}

}
