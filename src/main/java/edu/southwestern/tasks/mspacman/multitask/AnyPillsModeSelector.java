package edu.southwestern.tasks.mspacman.multitask;

/**
 * @author Jacob Schrum One module if any pills are present, and another module
 *         otherwise. Only makes sense given modified domain that allows
 *         evaluation in absence of pills.
 */
public class AnyPillsModeSelector extends MsPacManModeSelector {

	public static final int PILLS_PRESENT = 0;
	public static final int NO_PILLS_PRESENT = 1;

	public AnyPillsModeSelector() {
	}

	/**
	 * sets the game mode based on if there are any pills or not 0 if there are
	 * some pills 1 if there are no pills
	 * 
	 * @return mode
	 */
	public int mode() {
		return gs.getNumActivePills() > 0 || gs.getNumActivePowerPills() > 0 ? PILLS_PRESENT : NO_PILLS_PRESENT;
	}

	/**
	 * There are 2 modes for this mode selector
	 * 
	 * @return 2
	 */
	public int numModes() {
		return 2;
	}

	@Override
	/**
	 * gets the associated fitness scores with this mode selector
	 * 
	 * @return an int array holding the score for if there are some pills in the
	 *         first index and the score for if there are no pills in the second
	 *         index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[PILLS_PRESENT] = PILL_SCORE;
		result[NO_PILLS_PRESENT] = GHOST_SCORE;
		return result;
	}
}
