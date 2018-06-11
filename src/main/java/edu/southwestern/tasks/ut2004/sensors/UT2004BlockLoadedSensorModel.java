package edu.southwestern.tasks.ut2004.sensors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;
import java.util.ArrayList;

/**
 * Creates a sensor model with the necesarry sensor blocks
 * @author Jacob Schrum
 */
public abstract class UT2004BlockLoadedSensorModel implements UT2004SensorModel {

	ArrayList<UT2004SensorBlock> blocks;


	/**
	 * creates a new array of sensors called 'blocks'
	 */
	public UT2004BlockLoadedSensorModel() {
		blocks = new ArrayList<UT2004SensorBlock>();
	}

	/**
	 * prepares the blocks array
	 */
	public void prepareSensorModel(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		for (int i = 0; i < blocks.size(); i++) {
			blocks.get(i).prepareBlock(bot); //sets the array up to host sensors
		}
	}

	/**
	 * reads the inputs from the sensors and interprets them
	 * @return returns the data gathered in an array of inputs
	 */
	public double[] readSensors(@SuppressWarnings("rawtypes") UT2004BotModuleController bot) {
		double[] inputs = new double[numberOfSensors()];
		int in = 0;
		for (int i = 0; i < blocks.size(); i++) { //collects the data from the sensors
			in = blocks.get(i).incorporateSensors(bot, in, inputs);
		}
		return inputs;
	}

	/**
	 * creates a string array that stores the labels for the sensors in corresponding addresses to the blocks array
	 * @return returns the labels array
	 */
	public String[] sensorLabels() {
		String[] labels = new String[numberOfSensors()];//sets up the sensor array
		int in = 0;
		for (int i = 0; i < blocks.size(); i++) {//populates the array
			in = blocks.get(i).incorporateLabels(in, labels);
		}
		return labels;
	}

	/**
	 * @return returns the number of sensors in the block
	 */
	public int numberOfSensors() {
		int count = 0;
		for (int i = 0; i < blocks.size(); i++) {
			count += blocks.get(i).numberOfSensors(); //loops through the array to count the sensors
		}
		return count;
	}
}
