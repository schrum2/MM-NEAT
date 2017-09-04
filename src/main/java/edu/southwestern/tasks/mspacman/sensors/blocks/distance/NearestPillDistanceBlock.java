package edu.southwestern.tasks.mspacman.sensors.blocks.distance;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestPillDistanceBlock extends NearestDistanceBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePillsIndices();
	}

	@Override
	public String getType() {
		return "Pill";
	}
}
