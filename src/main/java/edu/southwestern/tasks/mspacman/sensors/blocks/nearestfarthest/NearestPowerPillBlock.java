package edu.southwestern.tasks.mspacman.sensors.blocks.nearestfarthest;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class NearestPowerPillBlock extends NearestFarthestIndexBlock {

	public NearestPowerPillBlock() {
		super();
	}

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}

	@Override
	public String typeOfTarget() {
		return "Power Pill";
	}
}
