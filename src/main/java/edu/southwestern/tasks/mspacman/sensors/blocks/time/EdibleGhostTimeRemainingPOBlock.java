package edu.southwestern.tasks.mspacman.sensors.blocks.time;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.blocks.MsPacManSensorBlock;

/**
 * This block senses the amount of remaining time that PacMan has the power pill buff.
 * @author pricew
 *
 */
public class EdibleGhostTimeRemainingPOBlock extends MsPacManSensorBlock {

	//These three are defined in pacman.game.Constants
	public final int EDIBLE_TIME = 200;
	public static final float EDIBLE_TIME_REDUCTION = 0.9f;
	public static final int LEVEL_RESET_REDUCTION = 6;
	
	
	@Override
	/**
	 * Calculates a normalized value of power pill buff time we have left and adds that sensor value
	 */
	public int incorporateSensors(double[] inputs, int startPoint, GameFacade gf, int lastDirection) {
		inputs[startPoint++] = gf.calculateRemainingPillBuffTime();		
		return startPoint;
	}

	@Override
	public int incorporateLabels(String[] labels, int in) {
		labels[in++] = "Power Pill Buff Time Remaining";
		return in;
	}

	@Override
	/**
	 * Returns the number of sensor readings we are adding. Here, we are only adding one sensor reading.
	 */
	public int numberAdded() {
		return 1;
	}

}
