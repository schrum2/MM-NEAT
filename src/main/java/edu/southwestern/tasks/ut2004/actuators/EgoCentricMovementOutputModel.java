package edu.southwestern.tasks.ut2004.actuators;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EgoCentricMovementAction;

/**
 *How the bot interprets output into actions with respect to itself
 *
 * @author Jacob Schrum
 */
public class EgoCentricMovementOutputModel implements UT2004OutputInterpretation {

	private final boolean utJumps;
	public static final int OUTPUT_INDEX_MOVEMENT = 0;
	public static final int OUTPUT_INDEX_TURNING = 1;
	public static final int  OUTPUT_INDEX_SHOOT = 2;
	public static final int OUTPUT_INDEX_JUMP = 3;
	

	/**
	 * sets the parameters of utJumps
	 */
	public EgoCentricMovementOutputModel() {
		this.utJumps = Parameters.parameters.booleanParameter("utJumps");
	}

	/**
	 * @return returns a 
	 */
	public String[] outputLabels() {
		return utJumps ? new String[] { "Towards/back movement", "Left/right turn", "Shoot", "Jump" }
				: new String[] { "Towards/back movement", "Left/right turn", "Shoot" };
	}

	/**
	 * Interprets given outputs as an action
	 * 
	 * @param bot (bot that will execute the action
	 * @param outputs (an array of numbers for the outputs)
	 */
	public BotAction interpretOutputs(@SuppressWarnings("rawtypes") UT2004BotModuleController bot, double[] outputs) {
		double moveForwardBackward = outputs[OUTPUT_INDEX_MOVEMENT];
		double turnLeftRight = outputs[OUTPUT_INDEX_TURNING];
		boolean shoot = outputs[OUTPUT_INDEX_SHOOT] > 0;
		boolean jump = utJumps ? outputs[OUTPUT_INDEX_JUMP] > 0 : false;
		return new EgoCentricMovementAction(moveForwardBackward, turnLeftRight, shoot, jump);
	}

	/**
	 * - Opponent relative left/right movement impulse - Opponent relative
	 * forward/back movement impulse - True/false shoot - True/false jump, if
	 * jumping allowed
	 *
	 * @return 
	 */
	public int numberOfOutputs() {
		return utJumps ? 4 : 3;
	}

	/**
	 * @return returns a copy of the output model
	 */
	public UT2004OutputInterpretation copy() {
		return new EgoCentricMovementOutputModel();
	}
}
