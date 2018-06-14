package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 * Supports popacman (TODO: test)
 * @author Jacob Schrum
 */
public class CountEdibleGhostsBlock extends TargetPortionRemainingBlock {

	public CountEdibleGhostsBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	/**
	 * Supports popacman
	 */
	public int getTargetMax(GameFacade gf) {
		return gf.getNumActiveGhosts();
	}

	@Override
	/**
	 * Supports popacman (TODO: test)
	 */
	public int getTargetCurrent(GameFacade gf) {
		return gf.getNumberOfEdibleGhosts();
	}

	@Override
	public String getTargetType() {
		return "Edible Ghost";
	}
}
