package edu.southwestern.tasks.mspacman.sensors.blocks.counting;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import oldpacman.game.Constants;

/**
 *
 * @author Jacob Schrum
 */
public class CountLairGhostsBlock extends TargetPortionRemainingBlock {

	public CountLairGhostsBlock(boolean portion, boolean inverse) {
		super(portion, inverse);
	}

	@Override
	public int getTargetMax(GameFacade gf) {
		return Constants.NUM_GHOSTS;
	}

	@Override
	public int getTargetCurrent(GameFacade gf) {
		return gf.getNumberOfLairGhosts();
	}

	@Override
	public String getTargetType() {
		return "Lair Ghost";
	}
}
