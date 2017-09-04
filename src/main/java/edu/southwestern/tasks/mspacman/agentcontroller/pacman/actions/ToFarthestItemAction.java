package edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ToFarthestItemAction implements MsPacManAction {

        @Override
	public int getMoveAction(GameFacade gf) {
		int[] targets = getTargets(gf);
		if (targets.length == 0) {
			return -1; // cede control to other action
		} else {
			int farthest = gf.getFarthestNodeIndexFromNodeIndex(gf.getPacmanCurrentNodeIndex(), targets);
			return gf.getNextPacManDirTowardsTarget(farthest);
		}
	}

	public abstract int[] getTargets(GameFacade gf);
}
