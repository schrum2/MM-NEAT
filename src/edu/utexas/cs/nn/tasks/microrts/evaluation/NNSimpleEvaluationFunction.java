package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.Arrays;

import edu.utexas.cs.nn.networks.Network;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

public class NNSimpleEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	private final int worker = 0;
	private final int light = 1;
	private final int heavy = 2;
	private final int ranged = 3;
	private final int bases = 4;
	private final int barracks = 5;
	private final int resources = 6;
	private final int hp = 7;
	private final int enemyAdjustment = 8; //inputs[8 ==> 15] are enemy versions of 0 ==> 7
	private final int workerDelta = 16;
	private final int mobileDelta = 17;
	
	/**
	 * counts the number of each unit belonging to each player
	 * 
	 * @param gs current game state
	 * @return	array containing input information
	 * 
	 * @author alicequint
	 */
	public double[] gameStateToArray(GameState gs){
		PhysicalGameState pgs = gs.getPhysicalGameState();
		double[] unitsOnBoard = new double[18]; //number of different variables we are tracking
		Unit currentUnit;
		int playerAdjustment;
		int enemyBaseLocation = -1; //not recorded yet. 
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : enemyAdjustment;
					double currentDistance;
					switch(currentUnit.getType().name){
					case "Worker":{
						unitsOnBoard[worker + playerAdjustment] ++; //0, 8
						unitsOnBoard[resources + playerAdjustment] += currentUnit.getResources(); //6, 14
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (1.0 + gs.getTime()); //+1 because game state time starts at 0.
							unitsOnBoard[workerDelta] += (currentDistance - unitsOnBoard[workerDelta]) / (1.0 + gs.getTime());
						}
						break;
					}
					case "Light": {
						unitsOnBoard[light + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (1.0 + gs.getTime()); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Heavy": {
						unitsOnBoard[heavy + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (1.0 + gs.getTime()); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Ranged": {
						unitsOnBoard[ranged + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (1.0 + gs.getTime()); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Base": {
						unitsOnBoard[bases + playerAdjustment]++; 
						unitsOnBoard[resources + playerAdjustment] += currentUnit.getResources();
						unitsOnBoard[hp + playerAdjustment] = currentUnit.getHitPoints();
						if(currentUnit.getPlayer() == 1){
							//record location so it can be used in other measurments
							enemyBaseLocation = currentUnit.getX() * pgs.getWidth() + currentUnit.getY();
						}
						break;
					}
					case "Barracks":{
						unitsOnBoard[barracks + playerAdjustment]++; 
						unitsOnBoard[hp + playerAdjustment] = currentUnit.getHitPoints();
						break;
					}
					default: break;
					}
				}
			}
		}
		//no longer normalized because the less concrete scores dont have the same max
//		unitsOnBoard = normalize(unitsOnBoard, pgs.getHeight()*pgs.getWidth());
		return unitsOnBoard;
	}
	
	private double distance(Unit currentUnit, int enemyBaseLocation){
		int baseY = enemyBaseLocation % pgs.getWidth();
		int baseX = (enemyBaseLocation - baseY) / pgs.getWidth();
		//distance: sq. root of ((x2-x1)^2 + (y2-y1)^2)
		return Math.abs(Math.sqrt((currentUnit.getX() - baseX) + (currentUnit.getY() - baseY)));
	}

	/**
	 * Normalize all values in the array to the range [0,1].
	 * 
	 * This kind of operation is so common that it should probably be defined in ArrayUtil instead.
	 * 
	 * @param data array whose values are normalized
	 * @param max Maximum raw score used for normalization
	 * @return Array of normalized value
	 */
	private double[] normalize (double[] data, int max){
		for(int i = 0; i < data.length; i++) {
			data[i] /= max;
		}
		return Arrays.copyOf(data, data.length);
	}

	public String[] sensorLabels() {
		return new String[]{"workers", "lights", "heavies", "ranged units", "bases", "barracks", "harvested resources", "buildings hp", "enemy workers", 
				"enemy lights", "enemy heavies", "enemy ranged units", "enemy bases", "enemy barracks", "enemy's harvested resources", "enemy buildings hp",
				"average worker distance from friendly base", "average mobile distance from enemy base",};
	}

	@Override
	public int getNumInputSubstrates() {
		return 1;
	}
}
