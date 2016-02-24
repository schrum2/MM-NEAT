package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock;
import edu.utexas.cs.nn.tasks.mspacman.sensors.directional.reachfirst.VariableDirectionCloserToTargetThanThreatGhostBlock;

/**
 * @author Jacob Schrum
 */
public class ThreatenedModeSelector extends MsPacManModeSelector {

    public static final int THREATENED = 0;
    public static final int SAFE = 1;
    private final int closeGhostDistance;

    public ThreatenedModeSelector() {
        super();
        closeGhostDistance = Parameters.parameters.integerParameter("closeGhostDistance");
    }

    public int mode() {
        int current = gs.getPacmanCurrentNodeIndex();
        int[] targets = gs.getThreatGhostLocations();
        if (targets.length == 0) {
            return SAFE;
        }
        int node = gs.getClosestNodeIndexFromNodeIndex(current, targets);
        double distance = gs.getShortestPathDistance(current, node);
        if (distance < closeGhostDistance) {
            return THREATENED;
        }
        // Somewhat analagous to counting the number of safe ants
        double safeDirs = 0;
        double options = 0;
        int[] neighbors = gs.neighbors(current);
        for (int i = 0; i < GameFacade.NUM_DIRS; i++) {
            if (neighbors[i] != -1) {
                VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock block = new VariableDirectionCloserToDepthTwoJunctionThanThreatGhostBlock(i);
                boolean safe = VariableDirectionCloserToTargetThanThreatGhostBlock.canReachAnyTargetSafelyInDirection(gs, block.getTargets(gs), i);
                if (safe) {
                    safeDirs++;
                }
                options++;
            }
        }
        // Is this the best threshold?
        return safeDirs/options > 0.3 ? SAFE : THREATENED;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[THREATENED] = GAME_SCORE;    // tentative
        result[SAFE] = GAME_SCORE;          // tentative
        return result;
    }
}
