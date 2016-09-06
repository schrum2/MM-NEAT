package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

/**
 * finds the sensor inputs for the prey by index. The inputs will be the X and Y
 * offsets to each prey
 * 
 * @author rollinsa
 *
 */
public class TorusPreyByIndexSensorBlock implements TorusPredPreySensorBlock {

	private int numPrey;

	public TorusPreyByIndexSensorBlock() {
		numPrey = Parameters.parameters.integerParameter("torusPreys");
	}

	@Override
	/**
	 * @return the sensor inputs to prey simply by their indices. The sensor
	 *         inputs are the X and Y offsets from this agent to each prey
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return NNTorusPredPreyController.getAgentOffsets(me, world, prey);
	}

	@Override
	/**
	 * @return the total number of sensors for the prey (X and Y offsets to each
	 *         prey)
	 */
	public int numSensors(boolean isPredator) {
		return isPredator ? (numPrey * 2) : (numPrey * 2 - 2);
	}

	@Override
	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels for this class will be the list of Prey being
	 * sensed (by index)
	 * 
	 * @return the sensorLabels for the prey by index
	 */
	public String[] sensorLabels(boolean isPredator) {
		return isPredator ? NNTorusPredPreyController.sensorLabels(numPrey, "Prey") : 
			NNTorusPredPreyController.sensorLabels(numPrey-1, "Prey");
	}

}
