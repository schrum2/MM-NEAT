package edu.utexas.cs.nn.tasks.mspacman.multitask;

import edu.utexas.cs.nn.parameters.Parameters;

/**
 * @author Jacob Schrum
 */
public class SpecificGhostProximityModeSelector extends MsPacManModeSelector {

    public static final int GHOST_CLOSE = 0;
    public static final int GHOST_FAR = 1;
    public final int crowdedDistance;
    public final int ghostIndex;

    public SpecificGhostProximityModeSelector() {
        this(3); // The random ghost in the Legacy team
    }

    public SpecificGhostProximityModeSelector(int ghostIndex) {
        this.ghostIndex = ghostIndex;
        this.crowdedDistance = Parameters.parameters.integerParameter("crowdedGhostDistance");
    }

    public int mode() {
        int pacman = gs.getPacmanCurrentNodeIndex();
        int ghost = gs.getGhostCurrentNodeIndex(ghostIndex);
        double distance = gs.getGhostLairTime(ghostIndex) > 0 ? crowdedDistance + 1 : gs.getShortestPathDistance(pacman, ghost);
        return distance > crowdedDistance ? GHOST_FAR : GHOST_CLOSE;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[GHOST_CLOSE] = GAME_SCORE;  // tentative
        result[GHOST_FAR] = GAME_SCORE;  // tentative
        return result;
    }
}
