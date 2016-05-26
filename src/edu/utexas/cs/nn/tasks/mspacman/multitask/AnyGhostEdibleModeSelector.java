package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * A Mode selector which selects modes based on if there are any edible ghosts at all or not
 * @author Jacob Schrum
 */
public class AnyGhostEdibleModeSelector extends MsPacManModeSelector {

    public static final int NO_EDIBLE_GHOSTS = 0;
    public static final int SOME_EDIBLE_GHOST = 1;
    public final boolean awardProperPowerPillEating;
    public final boolean punishImproperPowerPillEating;
    public final boolean levelObjective;

    /**
     * constructs this selector and also sets several objective parameter class values
     */
    public AnyGhostEdibleModeSelector() {
        this.awardProperPowerPillEating = Parameters.parameters.booleanParameter("awardProperPowerPillEating");
        this.punishImproperPowerPillEating = Parameters.parameters.booleanParameter("punishImproperPowerPillEating");
        this.levelObjective = Parameters.parameters.booleanParameter("levelObjective");
    }

    /**
     * returns what mode this will be based on if there are some edible ghosts or none
     * 0 if there are no edible ghosts
     * 1 if there are edible ghosts
     * @return mode
     */
    public int mode() {
        return gs.anyIsEdible() ? SOME_EDIBLE_GHOST : NO_EDIBLE_GHOSTS;
    }

    /**
     * There are 2 modes for this mode selector
     * @return 2
     */
    public int numModes() {
        return 2;
    }

    @Override
    /**
     * gets the associated fitness scores with this mode selector based on human selected parameters
     * @return an int array holding the score for no edible ghosts in the first index and the score
     * for some edible ghosts in the second index
     */
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[NO_EDIBLE_GHOSTS] = awardProperPowerPillEating ? PROPER_POWER_PILL_GHOST_COMBO : GAME_SCORE;
        result[SOME_EDIBLE_GHOST] = punishImproperPowerPillEating ? IMPROPER_POWER_PILL_GHOST_COMBO : (levelObjective ? GHOST_AND_LEVEL_COMBO : GHOST_SCORE);
        return result;
    }
}
