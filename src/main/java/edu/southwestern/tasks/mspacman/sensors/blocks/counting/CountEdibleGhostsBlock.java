package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;

/**
 *
 * @author Jacob Schrum
 */
public class CountEdibleGhostsBlock extends TargetPortionRemainingBlock {

	public CountEdibleGhostsBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	public int getTargetMax(GameFacade gf) {
		return gf.getNumActiveGhosts();
	}

	@Override
	public int getTargetCurrent(GameFacade gf) {
		return gf.getNumberOfEdibleGhosts();
	}

	@Override
	public String getTargetType() {
		return "Edible Ghost";
	}
}
