package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * @author Jacob Schrum
 */
public class GhostClusteringModeSelector extends MsPacManModeSelector {

    public static final int GHOSTS_CLUSTERED = 0;
    public static final int GHOSTS_SCATTERED = 1;
    public final int crowdedDistance;

    public GhostClusteringModeSelector() {
        crowdedDistance = Parameters.parameters.integerParameter("crowdedGhostDistance");
    }

    public int mode() {
        return isCrowded() ? GHOSTS_CLUSTERED : GHOSTS_SCATTERED;
    }
    
    /**
     * Notion of crowding stolen from Legacy2 ghosts
     * @param game
     * @return 
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

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[GHOSTS_CLUSTERED] = GAME_SCORE;  // tentative
        result[GHOSTS_SCATTERED] = GAME_SCORE;  // tentative
        return result;
    }
}
