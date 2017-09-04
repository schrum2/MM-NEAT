package edu.southwestern.tasks.gridTorus.sensors;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusWorld;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gridTorus.NNTorusPredPreyController;

/**
 * finds the sensor inputs for the predators by index. The inputs will be the X
 * and Y offsets to each predator
 * 
 * @author rollinsa
 *
 */
public class TorusPredatorsByIndexSensorBlock implements TorusPredPreySensorBlock {

	private int numPredators;

	public TorusPredatorsByIndexSensorBlock() {
		numPredators = Parameters.parameters.integerParameter("torusPredators");
	}

	@Override
	/**
	 * @return the sensor inputs for the predators by index. The inputs will be
	 *         the X and Y offsets to each predator from this agent.
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return NNTorusPredPreyController.getAgentOffsets(me, world, preds);
	}

	@Override
	/**
	 * @return the total number of sensors for the predators (X and Y offsets to
	 *         each pred)
	 */
	public int numSensors(boolean isPredator) {
		return isPredator ? (numPredators * 2 - 2) : (numPredators * 2);
	}

	@Override
	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels for this class will be the list of Predators
	 * being sensed (by index)
	 * 
	 * @return the sensorLabels for the predators by index
	 */
	public String[] sensorLabels(boolean isPredator) {
		return isPredator ? NNTorusPredPreyController.sensorLabels(numPredators-1, "Pred") : 
			NNTorusPredPreyController.sensorLabels(numPredators, "Pred");
	}

}