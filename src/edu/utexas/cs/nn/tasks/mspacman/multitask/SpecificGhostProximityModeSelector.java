package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * A Mode selector for a single specified ghost which selects between 2 modes
 * based on the following: 0) The specific ghost is close 1) The specific ghost
 * is far
 * 
 * @author Jacob Schrum
 */
public class SpecificGhostProximityModeSelector extends MsPacManModeSelector {

	public static final int GHOST_CLOSE = 0;
	public static final int GHOST_FAR = 1;
	public final int crowdedDistance;
	public final int ghostIndex;

	public SpecificGhostProximityModeSelector() {
		this(3); // The random ghost in the Legacy team
	}

	/**
	 * constructs this mode selector based off of the specified ghost and
	 * crowdedGhostDistance
	 * 
	 * @param ghostIndex
	 */
	public SpecificGhostProximityModeSelector(int ghostIndex) {
		this.ghostIndex = ghostIndex;
		this.crowdedDistance = Parameters.parameters.integerParameter("crowdedGhostDistance");
	}

	/**
	 * A Mode selector for a single specified ghost which selects between 2
	 * modes based on the following: 0) The specific ghost is close 1) The
	 * specific ghost is far
	 * 
	 * @return mode
	 */
	public int mode() {
		int pacman = gs.getPacmanCurrentNodeIndex();
		int ghost = gs.getGhostCurrentNodeIndex(ghostIndex);
		double distance = gs.getGhostLairTime(ghostIndex) > 0 ? crowdedDistance + 1
				: gs.getShortestPathDistance(pacman, ghost);
		return distance > crowdedDistance ? GHOST_FAR : GHOST_CLOSE;
	}

	/**
	 * There are 2 modes for this mode selector
	 * 
	 * @return 2
	 */
	public int numModes() {
		return 2;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for if the specific ghost is close
	 *         in the first index and the score for if the specific ghost is far
	 *         in the second index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[GHOST_CLOSE] = GAME_SCORE; // tentative
		result[GHOST_FAR] = GAME_SCORE; // tentative
		return result;
	}
}
