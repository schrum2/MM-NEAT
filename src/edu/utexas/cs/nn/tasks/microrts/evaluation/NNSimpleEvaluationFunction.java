package edu.utexas.cs.nn.tasks.microrts.evaluation;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NNSimpleEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	/**
	 * counts the number of each unit belonging to each player
	 * 
	 * @param gs current game state
	 * @return	array containing input information
	 */
	public double[] gameStateToArray(GameState gs){
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[] unitsOnBoard = new double[16];
		Unit currentUnit;
		int playerAdjustment;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : 8; //half of size
					switch(currentUnit.getType().name){
					case "Worker":{
						unitsOnBoard[0 + playerAdjustment]++; 
						unitsOnBoard[6 + playerAdjustment] += currentUnit.getResources();
						break;
					}
					case "Light": unitsOnBoard[1 + playerAdjustment]++; break;
					case "Heavy": unitsOnBoard[2 + playerAdjustment]++; break;
					case "Ranged": unitsOnBoard[3 + playerAdjustment]++; break;
					case "Base": {
						unitsOnBoard[4 + playerAdjustment]++; 
						unitsOnBoard[6 + playerAdjustment] += currentUnit.getResources();
						unitsOnBoard[7 + playerAdjustment] = currentUnit.getHitPoints();
						break;
					}
					case "Barracks":{
						unitsOnBoard[5 + playerAdjustment]++; 
						unitsOnBoard[7 + playerAdjustment] = currentUnit.getHitPoints();
						break;
					}
					default: break;
					}
				}
			}
		}
		//maybe also avg distance of workers from friendly base, avg. distance of workers from enemy base
		unitsOnBoard = normalize(unitsOnBoard, pgs.getHeight()*pgs.getWidth());
		return unitsOnBoard;
	}

	/**
	 * Normalize all values in the array to the range [0,1].
	 * 
	 * This kind of operation is so common that it should probably be defined in ArrayUtil instead.
	 * 
	 * @param data array whose values are normalized
	 * @param max Maximum raw score used for normalization
	 * @return Array of normalized values
	 * 
	 * HOWEVER: The original data is normalized too. Either do not return anything,
	 *          or use Arrays.copyOf to copy the array before returning the normalized version.
	 */
	private double[] normalize (double[] data, int max){
		for(int i = 0; i < data.length; i++) {
			data[i] /= max;
		}
		return data;
	}

	public String[] sensorLabels() {
		return new String[]{"workers", "lights", "heavies", "ranged-units", "bases", "barracks", "resources", "base-hp", "enemy-workers", 
				"enemy-lights", "enemy-heavies", "enemy-ranged-units", "enemy-bases", "enemy-barracks", "enemy-resources", "eney-base-hp"};
	}

	@Override
	public int getNumInputSubstrates() {
		return 1;
	}
}
