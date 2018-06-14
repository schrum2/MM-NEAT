package edu.southwestern.tasks.mspacman.sensors.blocks.time;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 * Gets the time remaining by getting the current time and subtracting that from the time limit.
 * @author Jacob
 */
public class TimeLeftBlock extends MsPacManSensorBlock {

	@Override
	public int incorporateSensors(double[] inputs, int in, GameFacade gf, int lastDirection) {
		int levelTime = gf.getCurrentLevelTime();
		inputs[in++] = (CommonConstants.pacManLevelTimeLimit - levelTime)
				/ (CommonConstants.pacManLevelTimeLimit * 1.0);
		return in;
	}

	@Override
	public int incorporateLabels(String[] labels, int startPoint) {
		labels[startPoint++] = "Time Remaining";
		return startPoint;
	}

	@Override
	public int numberAdded() {
		return 1;
	}
}
