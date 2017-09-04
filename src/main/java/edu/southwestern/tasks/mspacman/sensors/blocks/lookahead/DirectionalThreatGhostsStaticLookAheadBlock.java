/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.southwestern.tasks.mspacman.sensors.blocks.lookahead;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob
 */
public class DirectionalThreatGhostsStaticLookAheadBlock extends DirectionalStaticLookAheadBlock {

	public DirectionalThreatGhostsStaticLookAheadBlock() {
		super(false); // min threats along a route
	}

	@Override
	public String targetType() {
		return "Threat Ghosts";
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getThreatGhostLocations();
	}

	@Override
	public double maxCount(GameFacade gf) {
		return gf.getNumActiveGhosts();
	}
}
