package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum
 */
public class MazeModeSelector extends MsPacManModeSelector {

    public int mode() {
        return gs.getMazeIndex();
    }

    public int numModes() {
        return 4;
    }

    @Override
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        System.arraycopy(SPECIFIC_LEVELS, 0, result, 0, result.length);
        return result;
    }
}
