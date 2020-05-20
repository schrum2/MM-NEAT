package edu.southwestern.tasks.mspacman.facades;

import java.util.EnumMap;
import java.util.Map.Entry;

import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.tasks.popacman.ghosts.controllers.OldToNewGhostIntermediaryController;
import pacman.controllers.IndividualGhostController;
import pacman.game.Constants;

/**
 *Facade that allows ghosts to be
 *controlled. 
 * @author Jacob Schrum
 */
public class GhostControllerFacade {

	//actual ghost controller
	public oldpacman.controllers.NewGhostController oldG = null;
	//actual ghost controller for PO conditions
	public pacman.controllers.MASController poG = null;
	
	/**
	 * Constructor
	 * @param g ghostController
	 */
	public GhostControllerFacade(oldpacman.controllers.NewGhostController g) {
		oldG = g;
	}
	
	/**
	 * Used for Partially Observable Pacman
	 * Constructor
	 * @param g ghostController
	 */
	public GhostControllerFacade(pacman.controllers.MASController g) {
		poG = g;
	}
	
	public GhostControllerFacade(OldToNewGhostIntermediaryController g) {
		poG = g.MASController;
	}
	
	public GhostControllerFacade(EnumMap<Constants.GHOST,IndividualGhostController> map) {
		pacman.controllers.MASController g = new pacman.controllers.MASController(map);
		poG = g;
	}

	/**
	 * Gets actions available to ghost
	 * Supports popacman (TODO: test test test test)
	 * @param game game ghost is in
	 * @param timeDue time ghost has to make decision//TODO
	 * @return available actions
	 * @throws NoSuchFieldException 
	 */
	public int[] getActions(GameFacade game, long timeDue){
		return oldG == null ?
				moveEnumToArrayPO(poG.getMove(game.poG, timeDue)):
				moveEnumToArray(oldG.getMove(game.oldG, timeDue));
	}

	/**
	 * changes move enumerations into numerical array.
	 * Has a popacman version.
	 * @param moves possible moves
	 * @return integer representations of moves
	 */
	private int[] moveEnumToArray(EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> moves) {
			int[] result = new int[CommonConstants.numActiveGhosts];
			for (Entry<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> e : moves.entrySet()) {
				result[GameFacade.ghostToIndex(e.getKey())] = GameFacade.moveToIndex(e.getValue());
			}
			return result;
	}
	
	/**
	 * changes move enumerations into numerical array.
	 * Used fo popacman
	 * @param moves
	 * @return
	 */
	private int[] moveEnumToArrayPO(EnumMap<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE> moves) {
		int[] result = new int[CommonConstants.numActiveGhosts];
		for (Entry<pacman.game.Constants.GHOST, pacman.game.Constants.MOVE> e : moves.entrySet()) {
			result[GameFacade.ghostToIndexPO(e.getKey())] = GameFacade.moveToIndex(e.getValue());
		}
		return result;
}

	/**
	 * Resets ghost controller by resetting thread 
	 * This is terrible coding that needs to be fixed
	 * @throws NoSuchMethodException
	 */
	public void reset() {
		if(oldG == null) {
			//TODO:
		} else {
			oldG.reset();
		}
	}
}
