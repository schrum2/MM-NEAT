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

    public CompleteTaskModeSelector() {
        super();
        closeGhostDistance = Parameters.parameters.integerParameter("closeGhostDistance");
    }

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

    public int numModes() {
        return 3;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[EAT_GHOSTS] = IMPROPER_POWER_PILL_GHOST_COMBO;
        result[LURE] = LURING_FITNESS;
        result[EAT_PILLS] = PILL_AND_NO_POWER_PILL_COMBO;
        return result;
    }
}
