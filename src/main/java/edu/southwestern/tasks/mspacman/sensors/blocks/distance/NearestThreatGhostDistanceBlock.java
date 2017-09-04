package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestThreatGhostDistanceBlock extends NearestDistanceBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getThreatGhostLocations();
	}

	@Override
	public String getType() {
		return "Threat Ghost";
	}
}
