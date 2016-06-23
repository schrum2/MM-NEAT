package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;

public class BiasSensorBlock implements TorusPredPreySensorBlock{

	@Override
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return new double[]{1.0};
	}

	@Override
	public int numSensors() {
		return 1;
	}

	@Override
	public String[] sensorLabels() {
		return new String[]{"Bias"};
	}

}
