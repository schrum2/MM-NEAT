/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestPowerPillDistanceBlock extends NearestDistanceBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}

	@Override
	public String getType() {
		return "Power Pill";
	}
}
