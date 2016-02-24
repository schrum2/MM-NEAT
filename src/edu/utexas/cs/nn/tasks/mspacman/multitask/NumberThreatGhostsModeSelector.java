package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.CommonConstants;

/**
 * @author Jacob Schrum
 */
public class NumberThreatGhostsModeSelector extends MsPacManModeSelector {

    public static final int SOME_EDIBLE_GHOSTS = 0;
    public static final int SOME_THREAT_GHOSTS = 1;
    public static final int ALL_THREAT_GHOSTS = 2;
    
    public NumberThreatGhostsModeSelector() {
    }

    public int mode() {
        if(gs.anyIsEdible()) return SOME_EDIBLE_GHOSTS;
        int[] threats = gs.getThreatGhostLocations();
        return threats.length == CommonConstants.numActiveGhosts ? ALL_THREAT_GHOSTS : SOME_THREAT_GHOSTS;
    }

    public int numModes() {
        return 3;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[SOME_EDIBLE_GHOSTS] = GHOST_SCORE;
        result[SOME_THREAT_GHOSTS] = PILL_SCORE;
        result[ALL_THREAT_GHOSTS] = GAME_SCORE;
        return result;
    }
}
