package edu.southwestern.tasks.ut2004.actuators;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.OpponentRelativeMovementAction;

/**
 *Collects and interprets data on the opponent's movements to create a movement model 
 *
 * @author Jacob Schrum
 */
public class OpponentRelativeMovementOutputModel implements UT2004OutputInterpretation {

	public static int MEMORY_TIME = 30;
	public static final int OUTPUT_INDEX_MOVEMENT = 0;
	public static final int OUTPUT_INDEX_STRAFING = 1;
	public static final int  OUTPUT_INDEX_SHOOT = 2;
	public static final int OUTPUT_INDEX_JUMP = 3;

	public String[] outputLabels() {
		return new String[] { "Towards/back impulse", "Left/right impulse", "Shoot", "Jump" };
	}

	/**
	 * Collects Data on the opponent and interprets it
	 * 
	 * @returns returns an array of outputs based on the opponent's movements
	 */
	public BotAction interpretOutputs(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, double[] outputs) {
		Player opponent = bot.getPlayers().getNearestEnemy(MEMORY_TIME);
		double moveForwardBackward = outputs[OUTPUT_INDEX_MOVEMENT];
		double strafeLeftRight = outputs[OUTPUT_INDEX_STRAFING];
		boolean shoot = outputs[OUTPUT_INDEX_SHOOT] > 0;
		boolean jump = outputs[OUTPUT_INDEX_JUMP] > 0;
		return new OpponentRelativeMovementAction(opponent, moveForwardBackward, strafeLeftRight, shoot, jump);
	}

	/**
	 * - Opponent relative left/right movement impulse - Opponent relative
	 * forward/back movement impulse - True/false shoot - True/false jump
	 *
	 * @return returns whether or not the bot jumps
	 */
	public int numberOfOutputs() {
		return 4;
	}

	/**
	 * @return returns a copy of the movement model
	 */
	public UT2004OutputInterpretation copy() {
		return new OpponentRelativeMovementOutputModel();
	}
}
