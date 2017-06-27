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
	private static final int HP = 7;
	private static final int ENEMY_ADJUSTMENT = 8; //inputs[8 ==> 15] are enemy versions of 0 ==> 7
	private static final int WORKER_DELTA = 16;
	private static final int MOBILE_DELTA = 17;
	
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
					inputs[HP + playerAdjustment] = currentUnit.getHitPoints();
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
						inputs[HP + playerAdjustment] = currentUnit.getHitPoints();
						break;
					}
					default: break;
					}
				}
			}
		}
		// TODO: It is important to restrict the ranges of all inputs to the range -1 to 1.
		//       If you are uncertain about the range, then overestimate a bit (though not too much).
		//		 You can get a general idea of the range by printing out the values.
		//       You can also apply ActivationFunctions.tanh if the upper/lower bounds seem to trail off to large values,
		//       But divide by some normalizing value first.
		
		//no longer normalized because the less concrete scores dont have the same max
		//		unitsOnBoard = normalize(unitsOnBoard, pgs.getHeight()*pgs.getWidth());
		for(double i : inputs){
			System.out.print(i + " ");
		}
		System.out.println();
		MiscUtil.waitForReadStringAndEnterKeyPress(); //end dlet
		return inputs;
	}

	private double distance(Unit currentUnit, int enemyBaseLocation){
		int baseY = enemyBaseLocation % pgs.getWidth();
		int baseX = enemyBaseLocation / pgs.getWidth();
		//distance: sq. root of |((x2-x1)^2 + (y2-y1)^2)|
		return Math.sqrt(Math.abs((currentUnit.getX() - baseX)*(currentUnit.getX() - baseX) + (currentUnit.getY() - baseY)*(currentUnit.getY() - baseY)));
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
