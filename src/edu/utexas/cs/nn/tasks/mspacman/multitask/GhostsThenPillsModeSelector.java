package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class GhostsThenPillsModeSelector extends MsPacManModeSelector {

    public static final int CAN_EAT_GHOSTS = 0;
    public static final int NO_MORE_EDIBLE = 1;

    public int mode() {
        return gs.getNumActivePowerPills() > 0 || gs.anyIsEdible() ? CAN_EAT_GHOSTS : NO_MORE_EDIBLE;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[CAN_EAT_GHOSTS] = GHOST_SCORE;
        result[NO_MORE_EDIBLE] = PILL_SCORE;
        return result;
    }
}
