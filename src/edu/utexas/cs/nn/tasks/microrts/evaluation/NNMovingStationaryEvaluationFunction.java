package edu.utexas.cs.nn.tasks.microrts.evaluation;
//path: edu.utexas.cs.nn.tasks.microrts.evaluation.NNMovingStationaryEvaluationFunction
import micro.rts.GameState;
import micro.rts.units.Unit;

/**
 * 
 * @author alicequint
 * distinguishes between mobile and immobile units and puts them on their own substrates
 */
public class NNMovingStationaryEvaluationFunction extends NNEvaluationFunction{
	

	@Override
	protected double[] gameStateToArray(GameState gs) {
		pgs = gs.getPhysicalGameState();
		double[] inputs = new double[pgs.getWidth()*pgs.getHeight() * 2];
		Unit current = null;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				current = pgs.getUnitAt(i, j);
				if(current!= null){
					if(current.getType().canMove){
						inputs[i*pgs.getWidth() + j] = current.getID(); 
					} else {
						inputs[i*pgs.getWidth() + j + pgs.getWidth()*pgs.getHeight()] = current.getID();
					}
				}
			}
		}
		return inputs;
	}

	@Override
	public String[] sensorLabels() {
		assert pgs != null : "There must be a physical game state in order to extract height and width";
		String[] labels = new String[pgs.getWidth()*pgs.getHeight() * 2];
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				labels[i*pgs.getWidth() + j ] = "Mobile unit:  (" + i + ", " + j + ")";
				labels[i*pgs.getWidth() + j + pgs.getWidth()*pgs.getHeight()] = "Immobile unit:  (" + i + "," + j + ")";
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
