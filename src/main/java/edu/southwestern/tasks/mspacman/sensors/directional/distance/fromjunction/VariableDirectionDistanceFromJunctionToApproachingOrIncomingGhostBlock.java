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
public class VariableDirectionDistanceFromJunctionToApproachingOrIncomingGhostBlock
		extends VariableDirectionDistanceFromJunctionBlock {
	private final boolean threats;

	public VariableDirectionDistanceFromJunctionToApproachingOrIncomingGhostBlock(int dir, boolean threats) {
		super(dir);
		this.threats = threats;
	}

	public String getType() {
		return "Approaching/Incoming " + (threats ? "Threat" : "Edible") + " Ghost";
	}

	public int[] getTargets(GameFacade gf) {
		return threats ? gf.getApproachingOrIncomingThreatGhostLocations(dir)
				: gf.getApproachingOrIncomingEdibleGhostLocations(dir);
	}
}
