package edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class FromNearestThreatAction extends FromNearestItemAction {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getThreatGhostLocations();
	}
}
