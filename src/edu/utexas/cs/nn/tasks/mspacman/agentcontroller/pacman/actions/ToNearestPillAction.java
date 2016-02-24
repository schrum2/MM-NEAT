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
public class ToNearestPillAction extends ToNearestItemAction {

    @Override
    public int[] getTargets(GameFacade gf) {
        return gf.getActivePillsIndices();
    }
}
