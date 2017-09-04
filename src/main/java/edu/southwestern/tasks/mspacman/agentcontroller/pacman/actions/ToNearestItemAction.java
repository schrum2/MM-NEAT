/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ToNearestItemAction implements MsPacManAction {

	public int getMoveAction(GameFacade gf) {
		int[] targets = getTargets(gf);
		if (targets.length == 0) {
			return -1; // cede control to other action
		} else {
			int closest = gf.getClosestNodeIndexFromNodeIndex(gf.getPacmanCurrentNodeIndex(), targets);
			return gf.getNextPacManDirTowardsTarget(closest);
		}
	}

	public abstract int[] getTargets(GameFacade gf);
}
