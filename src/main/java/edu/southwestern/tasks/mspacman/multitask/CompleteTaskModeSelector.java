package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * Has three modes: 0) Ghosts are edible 1) No edible ghosts, and ghost is near
 * 2) No edible ghosts, and ghost not near
 *
 * @author Jacob Schrum
 */
public class CompleteTaskModeSelector extends MsPacManModeSelector {

	public static final int EAT_GHOSTS = 0;
	public static final int LURE = 1;
	public static final int EAT_PILLS = 2;
	public final int closeGhostDistance;

	/**
	 * constructs this selector and sets the definition of "closeGhostDistance"
	 * based on command line parameters
	 */
	public CompleteTaskModeSelector() {
		super();
		closeGhostDistance = Parameters.parameters.integerParameter("closeGhostDistance");
	}

	/**
	 * sets the game mode based on if the ghosts are edible, no ghosts are
	 * edible and a ghost is near, or no ghosts are edible and there are no
	 * ghosts near 0 if the ghosts are edible 1 if the ghosts are not edible and
	 * there is one near 2 if the ghosts are not edible and the ghosts are not
	 * near
	 * 
	 * @return mode
	 */
	public int mode() {
		// Eat edible ghosts
		if (gs.anyIsEdible()) {
			return EAT_GHOSTS;
		}
		// Eat pills when power pills are gone
		if (gs.getActivePowerPillsIndices().length == 0) {
			return EAT_PILLS;
		}
		int current = gs.getPacmanCurrentNodeIndex();
		int[] threatLocations = gs.getThreatGhostLocations();
		if (threatLocations.length == 0) {
			return EAT_PILLS;
		}
		int nearestGhost = gs.getClosestNodeIndexFromNodeIndex(current, threatLocations);
		double distance = gs.getPathDistance(current, nearestGhost);

		return distance < closeGhostDistance ? LURE : EAT_PILLS;
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
	 * gets the associated fitness scores with this mode selector based on human
	 * defined close ghost distance
	 * 
	 * @return an int array holding the score for if the ghosts are edible in
	 *         the first index, the score for if there are no edible ghosts and
	 *         a ghost is near in the second index, and the score for if there
	 *         are no edible ghosts and the ghosts are not near in the third
	 *         index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[EAT_GHOSTS] = IMPROPER_POWER_PILL_GHOST_COMBO;
		result[LURE] = LURING_FITNESS;
		result[EAT_PILLS] = PILL_AND_NO_POWER_PILL_COMBO;
		return result;
	}
}
