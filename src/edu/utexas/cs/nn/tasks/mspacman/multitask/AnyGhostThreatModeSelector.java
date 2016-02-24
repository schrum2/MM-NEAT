package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class AnyGhostThreatModeSelector extends MsPacManModeSelector {

    public static final int ALL_GHOSTS_EDIBLE = 0;
    public static final int SOME_GHOST_THREATENING = 1;

    public int mode() {
        return gs.getThreatGhostLocations().length > 0 ? SOME_GHOST_THREATENING : ALL_GHOSTS_EDIBLE;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[SOME_GHOST_THREATENING] = GAME_SCORE;
        result[ALL_GHOSTS_EDIBLE] = GHOST_SCORE;
        return result;
    }
}
