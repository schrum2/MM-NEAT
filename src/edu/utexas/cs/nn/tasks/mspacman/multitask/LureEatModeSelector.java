package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 *  A Mode selector which extends the AnyGhostEdibleModeSelector, which selects modes 
 *  based on if there are any edible ghosts at all or not
 * @author Jacob Schrum
 */
public class LureEatModeSelector extends AnyGhostEdibleModeSelector {

    @Override
    /**
     * gets the associated fitness scores with this mode selector
     * @return an int array holding the score for no edible ghosts in the first index based off of
     * the luring fitness score and the score for some edible ghosts in the second index
     */
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[NO_EDIBLE_GHOSTS] = LURING_FITNESS;
        result[SOME_EDIBLE_GHOST] = punishImproperPowerPillEating ? IMPROPER_POWER_PILL_GHOST_COMBO : GHOST_SCORE;
        return result;
    }
}
