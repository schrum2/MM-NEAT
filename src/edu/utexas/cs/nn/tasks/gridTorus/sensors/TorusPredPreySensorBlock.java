package edu.utexas.cs.nn.tasks.gridTorus.sensors;

import edu.utexas.cs.nn.gridTorus.TorusAgent;
import edu.utexas.cs.nn.gridTorus.TorusWorld;

/**
 * Interface to create a sensor block, which will be a set of sensors to either
 * predators or prey based on various factors defined in the command line
 * parameters. These blocks are then used individually or combined (also based
 * on parameters) in the NNTorusPredPreyController to create variations of
 * sensor inputs
 * 
 * @author rollinsa
 *
 */
public interface TorusPredPreySensorBlock {

	/**
	 * returns the sensor inputs
	 * 
	 * @param me
	 * @param world
	 * @param preds
	 * @param prey
	 * @return sensor inputs
	 */
	public double[] sensorValues(TorusAgent me, TorusWorld world, TorusAgent[] preds, TorusAgent[] prey);

	/**
	 * The number of sensors that will be given as sensor inputs according to
	 * just this individual sensor block if more sensor blocks are used in
	 * addition to the current one, then they are the total number of sensor
	 * inputs is summed up in the NNTorusPredPreyController to accommodate for
	 * this.
	 * @param prey 
	 * @param preds 
	 * @param me 
	 * @param isPredator 
	 * 
	 * @return the number of sensors
	 */
	public int numSensors(boolean isPredator);

	/**
	 * labels for each sensor input describing what that sensor input is for
	 * 
	 * @return sensor labels
	 */
	public String[] sensorLabels(boolean isPredator);
}
