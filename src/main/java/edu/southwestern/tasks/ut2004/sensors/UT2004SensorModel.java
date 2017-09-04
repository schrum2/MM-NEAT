package edu.southwestern.tasks.ut2004.sensors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;

/**
 *
 * @author He_Deceives
 */
public interface UT2004SensorModel {

	public void prepareSensorModel(UT2004BotModuleController bot);

	public double[] readSensors(UT2004BotModuleController bot);

	public String[] sensorLabels();

	public int numberOfSensors();

	public UT2004SensorModel copy();
}
