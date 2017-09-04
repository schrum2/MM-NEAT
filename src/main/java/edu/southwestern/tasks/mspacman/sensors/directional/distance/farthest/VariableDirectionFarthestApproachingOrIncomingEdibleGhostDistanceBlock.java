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
public class VariableDirectionFarthestApproachingOrIncomingEdibleGhostDistanceBlock
		extends VariableDirectionFarthestDistanceBlock {

	public VariableDirectionFarthestApproachingOrIncomingEdibleGhostDistanceBlock(int dir) {
		super(dir);
	}

	@Override
	public String getType() {
		return "Approaching/Incoming Edible Ghost";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getApproachingOrIncomingEdibleGhostLocations(dir);
	}
}
