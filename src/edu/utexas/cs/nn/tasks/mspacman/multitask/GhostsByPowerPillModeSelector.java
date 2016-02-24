package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

/**
 * Has three modes: 0) Ghosts are edible 1) No edible ghosts, and near power
 * pill 2) No edible ghosts, and not near power pill
 *
 * @author Jacob Schrum
 */
public class GhostsByPowerPillModeSelector extends MsPacManModeSelector {

    public static final int ONLY_PILLS_LEFT = 0;
    public static final int EAT_ALL_GHOSTS = 1;
    public static final int GHOSTS_EDIBLE = 2;
    public static final int EAT_POWER_PILL_THEN_GHOST = 3;
    public static final int FAR_FROM_POWER_PILLS = 4;

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
        return powerPillDistance < PowerPillAvoidanceBlock.CLOSE_DISTANCE && ghostDistance < 2 * PowerPillAvoidanceBlock.CLOSE_DISTANCE;
    }

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

    public int numModes() {
        return 5;
    }

    @Override
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
