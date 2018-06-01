package edu.southwestern.tasks.popacman.controllers;

import edu.southwestern.tasks.mspacman.facades.GameFacade;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import popacman.prediction.PillModel;

/**
 * a class that converts oldpacman controller information into popacman controller information
 * @author pricew
 *
 */
public class OldToNewPacManIntermediaryController extends pacman.controllers.PacmanController {

	protected final oldpacman.controllers.NewPacManController oldpacman;
	public PillModel pillModel = null;
	
	
	public OldToNewPacManIntermediaryController(oldpacman.controllers.NewPacManController oldpacman) {
		this.oldpacman = oldpacman;
	}
	
	@Override
	/**
	 * This method returns a popacman move. 
	 * Used for popacman
	 */
	public MOVE getMove(Game game, long timeDue) {
		//We need to pass the model of the game to the new gameFacade
		GameFacade informedGameFacade = new GameFacade(game);
		
		if(pillModel == null) {
			System.out.println("PillModel is null in OTNPMIC");
			this.pillModel = informedGameFacade.initPillModel();
			updateInformation(informedGameFacade);
		} else {
			System.out.println("PillModel is NOT null in OTNPMIC");
			informedGameFacade.setPillModel(this.pillModel);
			updateInformation(informedGameFacade);
		}
		
		//get the action to be made
		int action = oldpacman.getAction(informedGameFacade, timeDue);
		//converts an action to an oldpacman move to a popacman move to be returned
		return moveConverterOldToPO(GameFacade.indexToMove(action));
	}
	
	//This method handles the updating of all of the hidden information models in gf, retrieves
	//that information, and stores in in this class
	public void updateInformation(GameFacade gf) {
		this.pillModel = gf.updatePillModel();
	}
	
	//This method clears all stored information about a game
	public void clearStoredInformation() {
		this.pillModel = null;
	}
	
	/**
	 * Takes an oldpacman move and returns the equivalent popacman move
	 * @param move
	 * @return
	 * @throws NoSuchFieldException
	 * @author pricew
	 */
	public static pacman.game.Constants.MOVE moveConverterOldToPO(oldpacman.game.Constants.MOVE move){
		switch(move) {
			case NEUTRAL:
				return pacman.game.Constants.MOVE.NEUTRAL;
			case UP:
				return pacman.game.Constants.MOVE.UP;
			case LEFT:
				return pacman.game.Constants.MOVE.LEFT;
			case DOWN:
				return pacman.game.Constants.MOVE.DOWN;
			case RIGHT:
				return pacman.game.Constants.MOVE.RIGHT;
			default:
				System.out.println("ERROR in moveConverterOldPO, GAmeFacade.java");
				return null;
		}
	}


}
