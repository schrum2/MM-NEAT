package edu.southwestern.tasks.ut2004.actuators;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.ut2004.actions.BotAction;
import edu.southwestern.tasks.ut2004.actions.EgoCentricMovementAction;

/**
 *
 * @author Jacob Schrum
 */
public class EgoCentricMovementOutputModel implements UT2004OutputInterpretation {

	private final boolean utJumps;

	public EgoCentricMovementOutputModel() {
		this.utJumps = Parameters.parameters.booleanParameter("utJumps");
	}

	public String[] outputLabels() {
		return utJumps ? new String[] { "Left/right turn", "Towards/back movement", "Shoot", "Jump" }
				: new String[] { "Left/right turn", "Towards/back movement", "Shoot" };
	}

	public BotAction interpretOutputs(UT2004BotModuleController bot, double[] outputs) {
		double towards = outputs[0];
		double side = outputs[1];
		boolean shoot = outputs[2] > 0;
		boolean jump = utJumps ? outputs[3] > 0 : false;
		return new EgoCentricMovementAction(towards, side, shoot, jump);
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

	public UT2004OutputInterpretation copy() {
		return new EgoCentricMovementOutputModel();
	}
}
