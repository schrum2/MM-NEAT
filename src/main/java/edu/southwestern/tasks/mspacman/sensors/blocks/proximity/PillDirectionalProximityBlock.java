/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.proximity;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PillDirectionalProximityBlock extends DirectionalProximityBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}

	@Override
	public String targetType() {
		return "Pill";
	}
}
