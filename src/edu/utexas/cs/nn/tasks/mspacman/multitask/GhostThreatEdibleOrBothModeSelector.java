package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class GhostThreatEdibleOrBothModeSelector extends MsPacManModeSelector {

    public static final int EDIBLE_OR_LAIR = 0;
    public static final int THREAT_OR_LAIR = 1;
    public static final int MIXED = 2;

    public int mode() {
        int numThreats = 0;
        int numEdible = 0;
        int numLair = 0;
        for (int g = 0; g < gs.getNumActiveGhosts(); g++) {
            if (gs.isGhostThreat(g)) {
                numThreats++;
            } else if(gs.isGhostEdible(g)) {
                numEdible++;
            } else if(gs.ghostInLair(g)) {
                numLair++;
            }
        }
        int numGhosts = gs.getNumActiveGhosts();
        if (numGhosts == numThreats + numLair) {
            return THREAT_OR_LAIR; // all threats
        } else if (numGhosts == numEdible + numLair) {
            return EDIBLE_OR_LAIR; // no threats
        } else  {
            return MIXED; // mix
        }
    }

    public int numModes() {
        return 3;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[EDIBLE_OR_LAIR] = GHOST_SCORE;
        result[THREAT_OR_LAIR] = GAME_SCORE;
        result[MIXED] = GAME_SCORE;
        return result;
    }
}
