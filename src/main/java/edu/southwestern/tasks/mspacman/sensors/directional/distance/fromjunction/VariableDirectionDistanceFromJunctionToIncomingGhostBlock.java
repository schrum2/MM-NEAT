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
public class VariableDirectionDistanceFromJunctionToIncomingGhostBlock
		extends VariableDirectionDistanceFromJunctionBlock {
	private final boolean threat;

	public VariableDirectionDistanceFromJunctionToIncomingGhostBlock(int dir, boolean threat) {
		super(dir);
		this.threat = threat;
	}

	public String getType() {
		return "Incoming " + (threat ? "Threat" : "Edible") + " Ghost";
	}

	public int[] getTargets(GameFacade gf) {
		return threat ? gf.getIncomingThreatGhostLocations(dir) : gf.getIncomingEdibleGhostLocations(dir);
	}
}
