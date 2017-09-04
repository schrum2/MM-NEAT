/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionPillDistanceBlock extends VariableDirectionDistanceBlock {

	public VariableDirectionPillDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Pill";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}
}
