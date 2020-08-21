package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Supports PO pacman
 * @author Jacob Schrum
 */
public class PowerPillsRemainingBlock extends TargetPortionRemainingBlock {

	public PowerPillsRemainingBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	public int getTargetMax(GameFacade gf) {
		return gf.getNumberOfPowerPills();
	}

	@Override
	public int getTargetCurrent(GameFacade gf) {
		return gf.getActivePowerPillsIndices().length;
	}

	@Override
	public String getTargetType() {
		return "Power Pill";
	}
}
