package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * A Mode selector which selects modes based on if the ghosts are determined to be
 * crowded/clustered in an area or not. The ghosts are considered to be crowded if
 * their total distance is within the "crowdedGhostDistance" parameter threshold
 * defined as a command line parameter
 * @author Jacob Schrum
 */
public class GhostClusteringModeSelector extends MsPacManModeSelector {

    public static final int GHOSTS_CLUSTERED = 0;
    public static final int GHOSTS_SCATTERED = 1;
    public final int crowdedDistance;

    /**
     * constructs the mode selector and defines the threshold for crowded or scattered ghosts
     */
    public GhostClusteringModeSelector() {
        crowdedDistance = Parameters.parameters.integerParameter("crowdedGhostDistance");
    }

    /**
     * sets the game mode based on if the ghosts are clustered or scattered
     * 0 if the ghosts are clustered
     * 1 if the ghosts are scattered
     * @return mode
     */
    public int mode() {
        return isCrowded() ? GHOSTS_CLUSTERED : GHOSTS_SCATTERED;
    }
    
    /**
     * Notion of crowding stolen from Legacy2 ghosts
     * @param game
     * @return true if crowded, false if scattered
     */
    public boolean isCrowded() {
        float distance = 0;

        int numGhosts = gs.getNumActiveGhosts();
        for (int i = 0; i < numGhosts - 1; i++) {
            for (int j = i + 1; j < numGhosts; j++) {
                distance += gs.getShortestPathDistance(gs.getGhostCurrentNodeIndex(i), gs.getGhostCurrentNodeIndex(j));
            }
        }

        return (distance / 6) < crowdedDistance ? true : false;
    }
    
//    public boolean isCrowded() {
//        //double[][] pairDistances = new double[CommonConstants.numActiveGhosts][CommonConstants.numActiveGhosts];
//        int crowdedPairs = 0;
//        for (int i = 0; i < CommonConstants.numActiveGhosts; i++) {
//            for (int j = 0; j < CommonConstants.numActiveGhosts; j++) {
//                if (i < j) { // Because other info is redundant
//                    if (gs.getGhostLairTime(i) > 0 || gs.getGhostLairTime(j) > 0) {
//                        //pairDistances[i][j] = -1;
//                    } else {
//                        int g1 = gs.getGhostCurrentNodeIndex(i);
//                        int g2 = gs.getGhostCurrentNodeIndex(j);
//                        double distance = gs.getShortestPathDistance(g1, g2);
//                        if (distance < crowdedDistance) {
//                            crowdedPairs++;
//                            if (CommonConstants.watch) {
//                                gs.addLines(Color.red, g1, g2);
//                            }
//                        }
//                    }
//                    //pairDistances[i][j] = distance;
//                }
//            }
//        }
//
//        // Consider ghosts clustered if a majority are crowded together
//
//        return crowdedPairs > (CommonConstants.numActiveGhosts / 2.0);
//    }

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
     * @return an int array holding the score for if the ghosts are clustered in the first index and the score
     * for if the ghosts are scattered in the second index
     */
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[GHOSTS_CLUSTERED] = GAME_SCORE;  // tentative
        result[GHOSTS_SCATTERED] = GAME_SCORE;  // tentative
        return result;
    }
}
