package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

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
		return NNTorusPredPreyController.getPredatorOffsets(me, world, preds);
	}

	@Override
	/**
	 * @return the total number of sensors for the predators (X and Y offsets to
	 *         each pred)
	 */
	public int numSensors() {
		return numPredators * 2;
	}

	@Override
	/**
	 * Finds the sensor labels for this agent and returns them in an array of
	 * Strings The sensor labels for this class will be the list of Predators
	 * being sensed (by index)
	 * 
	 * @return the sensorLabels for the predators by index
	 */
	public String[] sensorLabels() {
		return NNTorusPredPreyController.sensorLabels(numPredators, "Pred");
	}

}