package edu.utexas.cs.nn.tasks.microrts.evaluation;

import java.util.Arrays;

import edu.utexas.cs.nn.networks.ActivationFunctions;
import edu.utexas.cs.nn.networks.Network;
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

	// TODO: Make these all static, and refactor so that the variable names are ALL_CAPS
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
		double[] unitsOnBoard = new double[18]; //number of different variables we are tracking // TODO: Make static final int with ALL_CAPS name
		unitsOnBoard[workerDelta] = 0;
		unitsOnBoard[mobileDelta] = 0;
		int workerDeltaUpdates = 0;
		int mobileDeltaUpdates = 0;
		int enemyBaseLocation = -1; //not recorded yet.
		int friendlyBaseLocation = -1;
		Unit currentUnit;
		int playerAdjustment;
		for(int i = 0; i < pgs.getWidth(); i++){
			for(int j = 0; j < pgs.getHeight(); j++){
				currentUnit = pgs.getUnitAt(i, j);
				if(currentUnit != null){
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : enemyAdjustment;
					unitsOnBoard[bases + playerAdjustment]++;
					unitsOnBoard[resources + playerAdjustment] += currentUnit.getResources();
					unitsOnBoard[hp + playerAdjustment] = currentUnit.getHitPoints();
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
					playerAdjustment = (currentUnit.getPlayer() == 0) ? 0 : enemyAdjustment;
					double currentDistance;
					switch(currentUnit.getType().name){
					case "Worker":{
						unitsOnBoard[worker + playerAdjustment] ++; //0, 8
						unitsOnBoard[resources + playerAdjustment] += currentUnit.getResources(); //6, 14
						if(currentUnit.getPlayer() == 0){
							if(enemyBaseLocation != -1){
								currentDistance = distance(currentUnit, enemyBaseLocation);
								//incremental calculation of the avg.
								unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (++mobileDeltaUpdates);
							}
							if(friendlyBaseLocation != -1){
								currentDistance = distance(currentUnit, enemyBaseLocation);
								//incremental calculation of the avg.
								unitsOnBoard[workerDelta] += (currentDistance - unitsOnBoard[workerDelta]) / (++workerDeltaUpdates);
							}
						}
						break;
					}
					case "Light": {
						unitsOnBoard[light + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Heavy": {
						unitsOnBoard[heavy + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Ranged": {
						unitsOnBoard[ranged + playerAdjustment]++; 
						if(currentUnit.getPlayer() == 0 && enemyBaseLocation != -1){
							currentDistance = distance(currentUnit, enemyBaseLocation);
							//incremental calculation of the avg.
							unitsOnBoard[mobileDelta] += (currentDistance - unitsOnBoard[mobileDelta]) / (++mobileDeltaUpdates); //+1 because game state time starts at 0.
						}
						break;
					}
					case "Barracks": {
						unitsOnBoard[barracks + playerAdjustment]++; 
						unitsOnBoard[hp + playerAdjustment] = currentUnit.getHitPoints();
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
		return unitsOnBoard;
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
