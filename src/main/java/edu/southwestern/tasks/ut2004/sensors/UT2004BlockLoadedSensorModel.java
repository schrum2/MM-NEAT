package edu.southwestern.tasks.ut2004.sensors;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.tasks.ut2004.sensors.blocks.UT2004SensorBlock;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public abstract class UT2004BlockLoadedSensorModel implements UT2004SensorModel {

	ArrayList<UT2004SensorBlock> blocks;

	public UT2004BlockLoadedSensorModel() {
		blocks = new ArrayList<UT2004SensorBlock>();
	}

	public void prepareSensorModel(UT2004BotModuleController bot) {
		for (int i = 0; i < blocks.size(); i++) {
			blocks.get(i).prepareBlock(bot);
		}
	}

	public double[] readSensors(UT2004BotModuleController bot) {
		double[] inputs = new double[numberOfSensors()];
		int in = 0;
		for (int i = 0; i < blocks.size(); i++) {
			in = blocks.get(i).incorporateSensors(bot, in, inputs);
		}
		return inputs;
	}

	public String[] sensorLabels() {
		String[] labels = new String[numberOfSensors()];
		int in = 0;
		for (int i = 0; i < blocks.size(); i++) {
			in = blocks.get(i).incorporateLabels(in, labels);
		}
		return labels;
	}

	public int numberOfSensors() {
		int count = 0;
		for (int i = 0; i < blocks.size(); i++) {
			count += blocks.get(i).numberOfSensors();
		}
		return count;
	}
}
