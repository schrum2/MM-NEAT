package edu.southwestern.tasks.mspacman.sensors.blocks.proximity;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class PowerPillDirectionalProximityBlock extends DirectionalProximityBlock {

	@Override
	public int[] getTargets(GameFacade gf) {
		return gf.getActivePowerPillsIndices();
	}

	@Override
	public String targetType() {
		return "Power Pill";
	}
}
