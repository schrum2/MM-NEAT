package edu.utexas.cs.nn.tasks.microrts;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NN2DEvaluationFunction<T extends Network> extends NNEvaluationFunction {

	/**
	 * counts all 
	 */
	@Override
	public double[] gameStateToArray(GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[] unitsOnBoard = new double[14];
		Unit currentUnit;
		int playerAdjustment;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : 6; //shift enemy units +6 in the array
					switch(currentUnit.getType().name){
					case "Worker": unitsOnBoard[0 + playerAdjustment]++; break;
					case "Light": unitsOnBoard[1 + playerAdjustment]++; break;
					case "Heavy": unitsOnBoard[2 + playerAdjustment]++; break;
					case "Ranged": unitsOnBoard[3 + playerAdjustment]++; break;
					case "Base": unitsOnBoard[4 + playerAdjustment]++; break;
					case "Barracks": unitsOnBoard[5 + playerAdjustment]++; break;
					default: break;
					}
				}
			}
		}
		return unitsOnBoard;
	}

	@Override
	public String[] sensorLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
