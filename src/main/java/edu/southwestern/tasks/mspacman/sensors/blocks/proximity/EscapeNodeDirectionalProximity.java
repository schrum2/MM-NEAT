/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.proximity;

import edu.southwestern.tasks.mspacman.data.EscapeNodes;
import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class EscapeNodeDirectionalProximity extends DirectionalProximityBlock {

	private final EscapeNodes escapeNodes;

	public EscapeNodeDirectionalProximity(EscapeNodes escapeNodes) {
		this.escapeNodes = escapeNodes;
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return escapeNodes.getNodes();
	}

	@Override
	public String targetType() {
		return "Escape Node";
	}
}
