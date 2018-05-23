package edu.southwestern.tasks.mspacman.facades;

import edu.southwestern.parameters.CommonConstants;
import oldpacman.controllers.NewGhostController;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

import java.util.EnumMap;
import java.util.Map.Entry;

/**
 *Facade that allows ghosts to be
 *controlled. 
 * @author Jacob Schrum
 */
public class GhostControllerFacade {

	//actual ghost controller
	NewGhostController newG = null;

	/**
	 * Constructor
	 * @param g ghostController
	 */
	public GhostControllerFacade(NewGhostController g) {
		newG = g;
	}

	/**
	 * Gets actions available to ghost
	 * @param game game ghost is in
	 * @param timeDue time ghost has to make decision//TODO
	 * @return available actions
	 */
	public int[] getActions(GameFacade game, long timeDue) {
		return moveEnumToArray(newG.getMove(game.newG, timeDue));
	}

	/**
	 * changes move enumerations into numerical array
	 * @param moves possible moves
	 * @return integer representations of moves
	 */
	private int[] moveEnumToArray(EnumMap<GHOST, MOVE> moves) {
		int[] result = new int[CommonConstants.numActiveGhosts];
		for (Entry<GHOST, MOVE> e : moves.entrySet()) {
			result[GameFacade.ghostToIndex(e.getKey())] = GameFacade.moveToIndex(e.getValue());
		}
		return result;
	}

	/**
	 * Resets ghost controller by resetting thread 
	 * This is terrible coding that needs to be fixed
	 * @throws NoSuchMethodException
	 */
	public void reset() {
		newG.reset();
	}
}
