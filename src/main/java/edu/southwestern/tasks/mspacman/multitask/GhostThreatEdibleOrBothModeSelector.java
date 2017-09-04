package edu.southwestern.tasks.mspacman.multitask;

/**
 * A Mode selector which selects between 3 modes based on the following: 0) The
 * ghosts are all edible or are in the lair 1) The ghosts are all a threat or
 * are in the lair 2) A mix. So the ghosts are either a threat or in the lair or
 * edible or in the lair
 * 
 * @author Jacob Schrum
 */
public class GhostThreatEdibleOrBothModeSelector extends MsPacManModeSelector {

	public static final int EDIBLE_OR_LAIR = 0;
	public static final int THREAT_OR_LAIR = 1;
	public static final int MIXED = 2;

	/**
	 * A Mode selector which selects between 3 modes based on the following: 0)
	 * The ghosts are all edible or are in the lair 1) The ghosts are all a
	 * threat or are in the lair 2) A mix. So the ghosts are either a threat or
	 * in the lair or edible or in the lair
	 * 
	 * @return mode
	 */
	public int mode() {
		int numThreats = 0;
		int numEdible = 0;
		int numLair = 0;
		for (int g = 0; g < gs.getNumActiveGhosts(); g++) {
			if (gs.isGhostThreat(g)) {
				numThreats++;
			} else if (gs.isGhostEdible(g)) {
				numEdible++;
			} else if (gs.ghostInLair(g)) {
				numLair++;
			}
		}
		int numGhosts = gs.getNumActiveGhosts();
		if (numGhosts == numThreats + numLair) {
			return THREAT_OR_LAIR; // all threats
		} else if (numGhosts == numEdible + numLair) {
			return EDIBLE_OR_LAIR; // no threats
		} else {
			return MIXED; // mix
		}
	}

	/**
	 * There are 3 modes for this mode selector
	 * 
	 * @return 3
	 */
	public int numModes() {
		return 3;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for if none of the ghosts are
	 *         threats in the first index and the score for if all of the ghosts
	 *         are threats in the second index and the score for if some of the
	 *         ghosts are threats or in lair and some are not threats or in lair
	 *         in the third index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[EDIBLE_OR_LAIR] = GHOST_SCORE;
		result[THREAT_OR_LAIR] = GAME_SCORE;
		result[MIXED] = GAME_SCORE;
		return result;
	}
}
