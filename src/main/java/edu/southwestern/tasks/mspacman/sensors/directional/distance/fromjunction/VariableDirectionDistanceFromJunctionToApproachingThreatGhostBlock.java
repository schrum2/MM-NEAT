/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.directional.distance.fromjunction;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class VariableDirectionDistanceFromJunctionToApproachingThreatGhostBlock
		extends VariableDirectionDistanceFromJunctionBlock {

	public VariableDirectionDistanceFromJunctionToApproachingThreatGhostBlock(int dir) {
		super(dir);
	}

	public String getType() {
		return "Approaching Threat Ghost";
	}

	public int[] getTargets(GameFacade gf) {
		return gf.getApproachingThreatGhostLocations();
	}
}
