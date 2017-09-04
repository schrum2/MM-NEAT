/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance.farthest;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionFarthestThreatGhostDistanceBlock extends VariableDirectionFarthestDistanceBlock {

	public VariableDirectionFarthestThreatGhostDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Threat Ghost";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getThreatGhostLocations();
	}
}
