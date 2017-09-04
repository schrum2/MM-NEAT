package edu.southwestern.tasks.mspacman.agentcontroller.pacman.actions;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Move directly away from the nearest item of a given type
 *
 * @author Jacob Schrum
 */
public abstract class FromNearestItemAction implements MsPacManAction {

        @Override
	public int getMoveAction(GameFacade gf) {
		int[] targets = getTargets(gf);
		if (targets.length == 0) {
			return -1; // cede control to other action
		} else {
			int closest = gf.getClosestNodeIndexFromNodeIndex(gf.getPacmanCurrentNodeIndex(), targets);
			return gf.getNextPacManDirAwayFromTarget(closest);
		}
	}

	public abstract int[] getTargets(GameFacade gf);
}
