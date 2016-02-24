package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * @author Jacob Schrum
 */
public class AnyGhostEdibleModeSelector extends MsPacManModeSelector {

    public static final int NO_EDIBLE_GHOSTS = 0;
    public static final int SOME_EDIBLE_GHOST = 1;
    public final boolean awardProperPowerPillEating;
    public final boolean punishImproperPowerPillEating;
    public final boolean levelObjective;

    public AnyGhostEdibleModeSelector() {
        this.awardProperPowerPillEating = Parameters.parameters.booleanParameter("awardProperPowerPillEating");
        this.punishImproperPowerPillEating = Parameters.parameters.booleanParameter("punishImproperPowerPillEating");
        this.levelObjective = Parameters.parameters.booleanParameter("levelObjective");
    }

    public int mode() {
        return gs.anyIsEdible() ? SOME_EDIBLE_GHOST : NO_EDIBLE_GHOSTS;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[NO_EDIBLE_GHOSTS] = awardProperPowerPillEating ? PROPER_POWER_PILL_GHOST_COMBO : GAME_SCORE;
        result[SOME_EDIBLE_GHOST] = punishImproperPowerPillEating ? IMPROPER_POWER_PILL_GHOST_COMBO : (levelObjective ? GHOST_AND_LEVEL_COMBO : GHOST_SCORE);
        return result;
    }
}
