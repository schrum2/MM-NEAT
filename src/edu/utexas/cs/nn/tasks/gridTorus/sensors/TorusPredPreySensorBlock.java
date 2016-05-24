package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;

public interface TorusPredPreySensorBlock {

	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey);
	
	public int numSensors();
	
	public String[] sensorLabels();
}
