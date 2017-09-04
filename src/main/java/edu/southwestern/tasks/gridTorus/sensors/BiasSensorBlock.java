package edu.southwestern.tasks.gridTorus.sensors;

import edu.southwestern.gridTorus.TorusAgent;
import edu.southwestern.gridTorus.TorusWorld;

public class BiasSensorBlock implements TorusPredPreySensorBlock{

	@Override
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey) {
		return new double[]{1.0};
	}

	@Override
	public int numSensors(boolean isPredator) {
		return 1;
	}

	@Override
	public String[] sensorLabels(boolean isPredator) {
		return new String[]{"Bias"};
	}

}
