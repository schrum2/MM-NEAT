package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FromNearestPowerPillAction extends FromNearestItemAction {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}
}
