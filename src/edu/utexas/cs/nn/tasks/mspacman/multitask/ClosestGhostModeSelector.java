package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.tasks.mspacman.ghosts.GhostComparator;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Jacob Schrum
 */
public class ClosestGhostModeSelector extends MsPacManModeSelector {

    public static final int CLOSEST_THREAT = 0;
    public static final int CLOSEST_EDIBLE = 1;

    public int mode() {
        ArrayList<Integer> ghosts = new ArrayList<Integer>(CommonConstants.numActiveGhosts);
        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
            ghosts.add(i);
        }
        Collections.sort(ghosts, new GhostComparator(gs, true, true));

        int count = 0;
        for (Integer g : ghosts) {
            count++;
            if (gs.isGhostEdible(g)) {
                // Nearest non-returning ghost is edible
                return CLOSEST_EDIBLE;
            } else if (gs.getNumNeighbours(gs.getGhostCurrentNodeIndex(g)) > 0 || count == 4) {
                // If not edible and not returning, then is a threat
                return CLOSEST_THREAT;
            }
        }
        return CLOSEST_THREAT;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[CLOSEST_THREAT] = GAME_SCORE;
        result[CLOSEST_EDIBLE] = GHOST_SCORE;
        return result;
    }
}
