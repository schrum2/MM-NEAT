/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.pacman.actions;

import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public abstract class ToFarthestItemAction implements MsPacManAction {

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
