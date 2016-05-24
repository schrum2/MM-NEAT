package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.gridTorus.NNTorusPredPreyController;

public class TorusPredatorsByIndexSensorBlock implements TorusPredPreySensorBlock {

	private int numPredators;
	
	public TorusPredatorsByIndexSensorBlock() {
		numPredators = Parameters.parameters.integerParameter("torusPredators");
	}
	
	@Override
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return NNTorusPredPreyController.getPredatorOffsets(me, world, prey);
	}

	@Override
	public int numSensors() {
		return numPredators * 2;
	}

	@Override
	public String[] sensorLabels() {
		return NNTorusPredPreyController.sensorLabels(numPredators, "Pred");
	}

}