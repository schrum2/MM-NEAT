package edu.utexas.cs.nn.tasks.mspacman.multitask;

/**
 * A Mode selector which selects between 2 modes based on the following:
 * 0) The threats are all incoming
 * 1) The threats are not all incoming (so there is an escape available)
 * @author Jacob Schrum
 */
public class IncomingThreatModeSelector extends MsPacManModeSelector {

	public static final int ALL_INCOMING = 0;
	public static final int ESCAPE_AVAILABLE = 1;

	/**
	 * A Mode selector which selects between 2 modes based on the following:
	 * 0) The threats are all incoming
	 * 1) The threats are not all incoming (so there is an escape available)
	 * @return mode
	 */
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
	 * @return an int array holding the score for if all threats are incoming in the first index and the score
	 * for if not all threats are incoming (if escape is available) in the second index
	 */
	public int[] associatedFitnessScores() {
		int[] result = new int[numModes()];
		result[ALL_INCOMING] = GAME_SCORE;
		result[ESCAPE_AVAILABLE] = GAME_SCORE;
		return result;
	}
}
