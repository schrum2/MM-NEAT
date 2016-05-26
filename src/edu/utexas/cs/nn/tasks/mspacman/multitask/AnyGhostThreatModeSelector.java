package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * A Mode selector which selects modes based on if all the ghosts are edible or if there are
 * some threatening ghosts currently in the game
 * @author Jacob Schrum
 */
public class AnyGhostThreatModeSelector extends MsPacManModeSelector {

    public static final int ALL_GHOSTS_EDIBLE = 0;
    public static final int SOME_GHOST_THREATENING = 1;

    /**
     * sets the game mode based on if there are some threatening ghosts or not
     * 0 if there are no threatening ghosts
     * 1 if there are some threatening ghosts
     * @return mode
     */
    public int mode() {
        return gs.getThreatGhostLocations().length > 0 ? SOME_GHOST_THREATENING : ALL_GHOSTS_EDIBLE;
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
     * gets the associated fitness scores with this mode selector 
     * @return an int array holding the score for if there are some threatening ghosts in the first index and the score
     * for if there are no threatening ghosts in the second index
     */
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[SOME_GHOST_THREATENING] = GAME_SCORE;
        result[ALL_GHOSTS_EDIBLE] = GHOST_SCORE;
        return result;
    }
}
