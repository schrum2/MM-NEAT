package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

public class TorusPreyByIndexSensorBlock implements TorusPredPreySensorBlock {

	private int numPrey;
	
	public TorusPreyByIndexSensorBlock() {
		numPrey = Parameters.parameters.integerParameter("torusPreys");
	}
	
	@Override
	/**
	 * returns the sensor inputs to prey simply by their indices. The sensor inputs are the distances from 
	 * this agent to all prey
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return NNTorusPredPreyController.getPreyOffsets(me, world, prey);
	}

	@Override
	public int numSensors() {
		return numPrey * 2;
	}

	@Override
	public String[] sensorLabels() {
		return NNTorusPredPreyController.sensorLabels(numPrey, "Prey");
	}

}
