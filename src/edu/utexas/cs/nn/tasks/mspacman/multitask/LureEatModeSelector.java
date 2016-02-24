package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class LureEatModeSelector extends AnyGhostEdibleModeSelector {

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[NO_EDIBLE_GHOSTS] = LURING_FITNESS;
        result[SOME_EDIBLE_GHOST] = punishImproperPowerPillEating ? IMPROPER_POWER_PILL_GHOST_COMBO : GHOST_SCORE;
        return result;
    }
}
