package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.tasks.mspacman.sensors.blocks.PowerPillAvoidanceBlock;

/**
 * Has three modes: 0) Ghosts are edible 1) No edible ghosts, and near power
 * pill 2) No edible ghosts, and not near power pill
 *
 * @author Jacob Schrum
 */
public class PreparingToChaseModeSelector extends MsPacManModeSelector {

    public static final int SOME_EDIBLE = 0;
    public static final int CLOSE_TO_POWER_PILL = 1;
    public static final int FAR_FROM_POWER_PILL = 2;
    private final int tooCloseDistance;

    public PreparingToChaseModeSelector() {
        super();
        this.tooCloseDistance = PowerPillAvoidanceBlock.CLOSE_DISTANCE;
    }

    public int mode() {
        for (int g = 0; g < gs.getNumActiveGhosts(); g++) {
            if (gs.isGhostEdible(g)) {
                // Any ghost edible
                return SOME_EDIBLE;
            }
        }
        // No ghosts were edible
        int[] powerPills = gs.getActivePowerPillsIndices();
        if (powerPills.length == 0) {
            return FAR_FROM_POWER_PILL;
        }
        int current = gs.getPacmanCurrentNodeIndex();
        int nearest = gs.getClosestNodeIndexFromNodeIndex(current, powerPills);
        double distance = gs.getPathDistance(current, nearest);

        return distance < tooCloseDistance ? CLOSE_TO_POWER_PILL : FAR_FROM_POWER_PILL;
    }

    public int numModes() {
        return 3;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[SOME_EDIBLE] = GHOST_SCORE;
        result[CLOSE_TO_POWER_PILL] = GAME_SCORE;
        result[FAR_FROM_POWER_PILL] = PILL_SCORE;
        return result;
    }
}
