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
		
		int currentLevelTime = gf.getCurrentLevelTime();
		int timeOfLastPowerPill = gf.getTimeOfLastPowerPillEaten();
		int levelCount = gf.getCurrentLevel();
		//The edible time of level "levelcount"
		double newEdibleTime = (int) (EDIBLE_TIME * (Math.pow(EDIBLE_TIME_REDUCTION, levelCount % LEVEL_RESET_REDUCTION)));
		
		//TODO: make this more sophisticated
		//If there is a visible threat ghost, assume we dont have edible power
//		if(gf.anyIsThreat()) {
//			inputs[startPoint++] = 0;
//		}
		
		int delta;
		
		//If we havent eaten a pill yet, we don't want to use -1
		//for calculating delta.
		if(timeOfLastPowerPill == -1) {
			delta = 0;
		} else {
			delta = (currentLevelTime - timeOfLastPowerPill);
		}
					
		//If we havent eaten a powerpill yet or it has been longer than edible time since we last ate a power pill
		if(timeOfLastPowerPill == -1 || delta > newEdibleTime) {
			//we have no power pill buff time
			inputs[startPoint++] = 0;
		} else {
			//we have a fraction of the whole power pill buff time left, normalized to EDIBLE_TIME. 
			//This ensures that the edible time seems shorter to the agent on further levels.
			inputs[startPoint++] = (newEdibleTime - delta) / EDIBLE_TIME;
		}
		
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
