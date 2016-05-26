package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * A Mode selector which selects between 4 modes based on the maze index
 * @author Jacob Schrum
 */
public class MazeModeSelector extends MsPacManModeSelector {

	/**
	 * A Mode selector which selects between 4 modes based on the maze index
	 * @return mode
	 */
    public int mode() {
        return gs.getMazeIndex();
    }

    /**
     * There are 4 modes for this mode selector
     * @return 4
     */
    public int numModes() {
        return 4;
    }

    @Override
    /**
     * gets the associated fitness scores with this mode selector 
     * @return an int array with each index holding the associated fitness score based on
     * the specific levels in order
     */
    public int[] associatedFitnessScores() {
        int[] result = new int[numModes()];
        System.arraycopy(SPECIFIC_LEVELS, 0, result, 0, result.length);
        return result;
    }
}
