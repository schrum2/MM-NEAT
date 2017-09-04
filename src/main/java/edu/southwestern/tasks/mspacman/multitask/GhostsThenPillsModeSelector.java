package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * A Mode selector which selects modes based on if pacMan has the possibility of
 * eating a ghost or if there is no possibility that pacMan can eat another
 * ghost (if there are no more power pills left and pacMan can't currently eat
 * any ghosts as is)
 * 
 * @author Jacob Schrum
 */
public class GhostsThenPillsModeSelector extends MsPacManModeSelector {

	public static final int CAN_EAT_GHOSTS = 0;
	public static final int NO_MORE_EDIBLE = 1;

	/**
	 * sets the game mode based on if the ghosts can ever be eaten or not 0 if
	 * the ghosts can possibly be eaten 1 if the ghosts can never be eaten
	 * 
	 * @return mode
	 */
	public int mode() {
		return gs.getNumActivePowerPills() > 0 || gs.anyIsEdible() ? CAN_EAT_GHOSTS : NO_MORE_EDIBLE;
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
	 * @return an int array holding the score for if pacMan can eat ghosts in
	 *         the first index and the score for if there are no more possible
	 *         edible ghosts (ever) in the second index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[CAN_EAT_GHOSTS] = GHOST_SCORE;
		result[NO_MORE_EDIBLE] = PILL_SCORE;
		return result;
	}
}
