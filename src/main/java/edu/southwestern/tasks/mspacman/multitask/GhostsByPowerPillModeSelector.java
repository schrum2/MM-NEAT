package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 * A Mode selector which selects between 5 modes respectively based on: 0) if
 * there are many edible ghosts on the path (at least three) 1) if there are any
 * edible ghosts 2) if there are only pills left (no power pills) 3) if pacMan
 * is close to a power pill and the ghosts 4) if pacMan is far from the power
 * pills
 *
 * @author Jacob Schrum
 */
public class GhostsByPowerPillModeSelector extends MsPacManModeSelector {

	public static final int ONLY_PILLS_LEFT = 0;
	public static final int EAT_ALL_GHOSTS = 1;
	public static final int GHOSTS_EDIBLE = 2;
	public static final int EAT_POWER_PILL_THEN_GHOST = 3;
	public static final int FAR_FROM_POWER_PILLS = 4;

	/**
	 * Selects the mode from 5 possible modes respectively (meaning that it
	 * prioritizes the modes in the following chronological order): 1) if there
	 * are many edible ghosts on the path (at least three) 2) if there are any
	 * edible ghosts 0) if there are only pills left (no power pills) 3) if
	 * pacMan is close to a power pill and the ghosts 4) if pacMan is far from
	 * the power pills
	 *
	 * @return mode
	 */
	public int mode() {
		if (numberOfEdibleGhostsOnPath() >= 3) {
			return EAT_ALL_GHOSTS;
		} else if (gs.anyIsEdible()) {
			return GHOSTS_EDIBLE;
		} else if (gs.getNumActivePowerPills() == 0) {
			return ONLY_PILLS_LEFT;
		} else if (closeToPowerPillAndGhosts()) {
			return EAT_POWER_PILL_THEN_GHOST;
		} else {
			return FAR_FROM_POWER_PILLS;
		}
	}

	/**
	 * determines if pacMan is close to a power pill and some ghosts
	 * 
	 * @return true if close to both, false if not
	 */
	public boolean closeToPowerPillAndGhosts() {
		int[] powerPills = gs.getActivePowerPillsIndices();
		int current = gs.getPacmanCurrentNodeIndex();
		int nearestPowerPill = gs.getClosestNodeIndexFromNodeIndex(current, powerPills);
		int[] ghosts = gs.getThreatGhostLocations();
		if (ghosts.length == 0) {
			return false;
		}
		int nearestGhost = gs.getClosestNodeIndexFromNodeIndex(current, ghosts);
		double powerPillDistance = gs.getPathDistance(current, nearestPowerPill);
		double ghostDistance = gs.getPathDistance(current, nearestGhost);
		return powerPillDistance < PowerPillAvoidanceBlock.CLOSE_DISTANCE
				&& ghostDistance < 2 * PowerPillAvoidanceBlock.CLOSE_DISTANCE;
	}

	/**
	 * finds the number of edible ghosts on pacMan's path
	 * 
	 * @return int of number of edible ghosts on path
	 */
	public int numberOfEdibleGhostsOnPath() {
		int[] ghosts = gs.getEdibleGhostLocations();
		if (ghosts.length == 0) {
			return 0;
		}
		final int current = gs.getPacmanCurrentNodeIndex();
		int farthest = gs.getFarthestNodeIndexFromNodeIndex(current, ghosts);
		int[] targetItems = gs.getEdibleGhostLocations();
		int[] path = gs.getShortestPath(current, farthest);
		int[] intersection = ArrayUtil.intersection(path, targetItems);
		return intersection.length;
	}

	/**
	 * There are 5 modes for this mode selector
	 * 
	 * @return 5
	 */
	public int numModes() {
		return 5;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for the various modes in the
	 *         following indices: 0: if there are only pill left (no power
	 *         pills) 1: if there are many edible ghosts on the path (at least
	 *         three) 2: if there are any edible ghosts 3: if pacMan is close to
	 *         a power pill and the ghosts 4: if pacMan is far from the power
	 *         pills
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[ONLY_PILLS_LEFT] = PILL_SCORE;
		result[EAT_ALL_GHOSTS] = GHOST_SCORE;
		result[GHOSTS_EDIBLE] = GHOST_SCORE;
		result[EAT_POWER_PILL_THEN_GHOST] = GHOST_SCORE;
		result[FAR_FROM_POWER_PILLS] = GAME_SCORE;
		return result;
	}
}
