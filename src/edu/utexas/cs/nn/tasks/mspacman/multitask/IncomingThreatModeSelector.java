package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class IncomingThreatModeSelector extends MsPacManModeSelector {

    public static final int ALL_INCOMING = 0;
    public static final int ESCAPE_AVAILABLE = 1;
    
    public int mode() {
        int current = gs.getPacmanCurrentNodeIndex();
        int[] neighbors = gs.neighbors(current);
        for(int i = 0; i < neighbors.length; i++) {
            if(neighbors[i] != -1) {
                if(!gs.isThreatIncoming(i)){
                    return ESCAPE_AVAILABLE;
                }
            }
        }
        return ALL_INCOMING;
    }

    public int numModes() {
        return 2;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        result[ALL_INCOMING] = GAME_SCORE;
        result[ESCAPE_AVAILABLE] = GAME_SCORE;
        return result;
    }
}
