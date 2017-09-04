package edu.southwestern.tasks.mspacman.objectives;

import edu.southwestern.networks.Network;
import java.util.List;

/**
 *
 * @author Jacob Schrum
 */
public class EligibilityTimeFramesGhostScore<T extends Network> extends EligibilityTimeFramesScore<T> {

	public EligibilityTimeFramesGhostScore(int mode) {
		super(mode);
	}

	@Override
	public List<Integer> eatTimes() {
		return g.getGhostEatTimes();
	}
}
