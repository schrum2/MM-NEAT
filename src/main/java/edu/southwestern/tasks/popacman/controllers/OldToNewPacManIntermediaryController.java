package edu.southwestern.tasks.popacman.controllers;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * a class that converts oldpacman controller information into popacman controller information
 * @author pricew
 *
 */
public class OldToNewPacManIntermediaryController extends pacman.controllers.PacmanController {

	protected final oldpacman.controllers.NewPacManController oldpacman;
	
	public OldToNewPacManIntermediaryController(oldpacman.controllers.NewPacManController oldpacman) {
		this.oldpacman = oldpacman;
	}
	
	@Override
	/**
	 * This method returns a popacman move. 
	 * Used for popacman
	 */
	public MOVE getMove(Game game, long timeDue) {
		//get the action to be made
		int action = oldpacman.getAction(new GameFacade(game), timeDue);
		//converts an action to an oldpacman move to a popacman move to be returned
		return GameFacade.moveConverterOldToPO(GameFacade.indexToMove(action));
	}
	


}
