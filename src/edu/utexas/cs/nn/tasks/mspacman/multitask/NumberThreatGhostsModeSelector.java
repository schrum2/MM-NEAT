package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 * A Mode selector which selects between 3 modes based on the following:
 * 0) Some ghosts are edible
 * 1) Some ghosts are threats
 * 2) All ghosts are threats
 * @author Jacob Schrum
 */
public class NumberThreatGhostsModeSelector extends MsPacManModeSelector {

	public static final int SOME_EDIBLE_GHOSTS = 0;
	public static final int SOME_THREAT_GHOSTS = 1;
	public static final int ALL_THREAT_GHOSTS = 2;

	public NumberThreatGhostsModeSelector() {
	}

	/**
	 * A Mode selector which selects between 3 modes based on the following:
	 * 0) Some ghosts are edible
	 * 1) Some ghosts are threats
	 * 2) All ghosts are threats
	 * @return mode
	 */
	public int mode() {
		if(gs.anyIsEdible()) return SOME_EDIBLE_GHOSTS;
		int[] threats = gs.getThreatGhostLocations();
		return threats.length == CommonConstants.numActiveGhosts ? ALL_THREAT_GHOSTS : SOME_THREAT_GHOSTS;
	}

    /**
     * There are 3 modes for this mode selector
     * @return 3
     */
	public int numModes() {
		return 3;
	}

	@Override
    /**
     * gets the associated fitness scores with this mode selector 
     * @return an int array holding the score for if some of the ghosts are edible in the first index and the score
     * for if some of the ghosts are threats in the second index and the score for if all of the ghosts are threats
     * in the third index
     */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[SOME_EDIBLE_GHOSTS] = GHOST_SCORE;
		result[SOME_THREAT_GHOSTS] = PILL_SCORE;
		result[ALL_THREAT_GHOSTS] = GAME_SCORE;
		return result;
	}
}
