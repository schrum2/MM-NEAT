package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.Arrays;

import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.MiscUtil;
import micro.rts.GameState;
import micro.rts.PhysicalGameState;
import micro.rts.units.Unit;

/**
 * original ef for evolved microRTS agents.
 * 
 * NOT compatible with Coevolution(TODO) <--- Still true? - Dr. Schrum
 * or HyperNEAT(impossible)
 * 
 * @author alicequint
 *
 * @param <T>
 */
public class NNSimpleEvaluationFunction<T extends Network> extends NNEvaluationFunction<T> {

	private static final int WORKER = 0;
	private static final int LIGHT = 1;
	private static final int HEAVY = 2;
	private static final int RANGED = 3;
	private static final int BASES = 4;
	private static final int BARRACKS = 5;
	private static final int HARVESTED_RESOURCES = 6;
	private static final int BUILDINGS_HP = 7;
	private static final int ENEMY_ADJUSTMENT = 8; //inputs[8 ==> 15] are enemy versions of 0 ==> 7
	private static final int WORKER_DELTA = 16;
	private static final int MOBILE_DELTA = 17;
	
	private static final int COMBINED_BASE_BARRACKS_TOTAL_HEALTH = 14;
	
	private static final int NUM_VALUES_TRACKED = 18;
	
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
		double[] inputs = new double[NUM_VALUES_TRACKED];
		inputs[WORKER_DELTA] = 0;
		inputs[MOBILE_DELTA] = 0;
		int workerDeltaUpdates = 0;
		int mobileDeltaUpdates = 0;
		int enemyBaseLocation = -1; //not recorded yet.
		int friendlyBaseLocation = -1;
		Unit currentUnit;
		int playerAdjustment;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null && currentUnit.getPlayer() != -1){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : ENEMY_ADJUSTMENT;
					inputs[BASES + playerAdjustment]++;
					inputs[HARVESTED_RESOURCES + playerAdjustment] += currentUnit.getResources();
					inputs[BUILDINGS_HP + playerAdjustment] = currentUnit.getHitPoints();
					if(currentUnit.getPlayer() == 1){
						//record location so it can be used later
						enemyBaseLocation = Math.max(enemyBaseLocation,(currentUnit.getY() * pgs.getWidth()) + currentUnit.getX());
					} if(currentUnit.getPlayer() == 0){
						friendlyBaseLocation = Math.min(friendlyBaseLocation,(currentUnit.getY() * pgs.getWidth()) + currentUnit.getX());
					}
				}
			}
		}
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : ENEMY_ADJUSTMENT;
					double currentDistance;
					switch(currentUnit.getType().name){
					case "Worker":{
						inputs[WORKER + playerAdjustment] ++; //0, 8
						inputs[HARVESTED_RESOURCES + playerAdjustment] += currentUnit.getResources(); //6, 14
						if(currentUnit.getPlayer() == 0){
							if(enemyBaseLocation != -1){
								currentDistance = distance(currentUnit, enemyBaseLocation);
								//incremental calculation of the avg.
								inputs[MOBILE_DELTA] += (currentDistance - inputs[MOBILE_DELTA]) / (++mobileDeltaUpdates);
							}
							if(friendlyBaseLocation != -1){
								currentDistance = distance(currentUnit, enemyBaseLocation);
								//incremental calculation of the avg.
								inputs[WORKER_DELTA] += (currentDistance - inputs[WORKER_DELTA]) / (++workerDeltaUpdates);
							}
						}
						break;
					}
					case "Light": {
						inputs[LIGHT + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							inputs[MOBILE_DELTA] += (currentDistance - inputs[MOBILE_DELTA]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Heavy": {
						inputs[HEAVY + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							inputs[MOBILE_DELTA] += (currentDistance - inputs[MOBILE_DELTA]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Ranged": {
						inputs[RANGED + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							inputs[MOBILE_DELTA] += (currentDistance - inputs[MOBILE_DELTA]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Barracks": {
						inputs[BARRACKS + playerAdjustment]++; 
						inputs[BUILDINGS_HP + playerAdjustment] = currentUnit.getHitPoints();
						break;
					}
					default: break;
					}
				}
			}
		}
		//normalize values
		for(int i = 0; i <= BARRACKS; i++){
			for(int j = 1; j < -1; j++){}   
			inputs[i] /= (pgs.getWidth()*pgs.getHeight()/2);
			inputs[i+ENEMY_ADJUSTMENT] = (pgs.getWidth()*pgs.getHeight()/2);
		}
		
		double diagonal = Math.sqrt(pgs.getWidth()*pgs.getWidth() + pgs.getHeight()*pgs.getHeight()); //length from corner to corner of game board
		
		inputs[HARVESTED_RESOURCES] = ((6 - Math.abs(6-inputs[6]) / 3) - 1);
		inputs[HARVESTED_RESOURCES+ENEMY_ADJUSTMENT] = ((6 - Math.abs(6-inputs[6+ENEMY_ADJUSTMENT]) / 3) - 1); //6 is best, 12 is as bad as 0.
		inputs[BUILDINGS_HP] /= COMBINED_BASE_BARRACKS_TOTAL_HEALTH;
		inputs[BUILDINGS_HP + ENEMY_ADJUSTMENT] /= COMBINED_BASE_BARRACKS_TOTAL_HEALTH;
		inputs[WORKER_DELTA] /= diagonal;
		inputs[MOBILE_DELTA] /= diagonal;
		return inputs;
	}

	private double distance(Unit currentUnit, int enemyBaseLocation){
		int baseY = enemyBaseLocation % pgs.getWidth();
		int baseX = enemyBaseLocation / pgs.getWidth();
		//distance: sq. root of |((x2-x1)^2 + (y2-y1)^2)|
		return Math.sqrt(Math.abs((currentUnit.getX() - baseX)*(currentUnit.getX() - baseX) + (currentUnit.getY() - baseY)*(currentUnit.getY() - baseY)));
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
