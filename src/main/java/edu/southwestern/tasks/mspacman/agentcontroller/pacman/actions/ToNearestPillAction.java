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
public class ToNearestPillAction extends ToNearestItemAction {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}
}
