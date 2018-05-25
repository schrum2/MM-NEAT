package edu.southwestern.tasks.popacman.controllers;

import java.util.EnumMap;
import pacman.controllers.IndividualGhostController;

public class GhostTeamController {

	protected final IndividualGhostController BLINKY;
	protected final IndividualGhostController PINKY;
	protected final IndividualGhostController INKY;
	protected final IndividualGhostController SUE;
	
	public GhostTeamController(IndividualGhostController BLINKY, IndividualGhostController PINKY, IndividualGhostController INKY, IndividualGhostController SUE) {
		this.BLINKY = BLINKY;
		this.PINKY = PINKY;
		this.INKY = INKY;
		this.SUE = SUE;
	}
	
	/**
	 * Gets the moves of each individual ghost controller (popacman code) and converts the move to an oldpacman move.
	 * Afterward, puts the moves in an EnumMap to be returned.
	 * @param game
	 * @param timeDue
	 * @return
	 * @author pricew
	 * @throws NoSuchFieldException 
	 */
	public EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> getMove(pacman.game.Game game, long timeDue){

		EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> moves = new EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE>(oldpacman.game.Constants.GHOST.class);
		
		oldpacman.game.Constants.MOVE blinkyMove = edu.southwestern.tasks.mspacman.facades.GameFacade.moveConverterPOOld(BLINKY.getMove(game, timeDue));
		oldpacman.game.Constants.MOVE pinkyMove = edu.southwestern.tasks.mspacman.facades.GameFacade.moveConverterPOOld(PINKY.getMove(game, timeDue));
		oldpacman.game.Constants.MOVE inkyMove = edu.southwestern.tasks.mspacman.facades.GameFacade.moveConverterPOOld(INKY.getMove(game, timeDue));
		oldpacman.game.Constants.MOVE sueMove = edu.southwestern.tasks.mspacman.facades.GameFacade.moveConverterPOOld(SUE.getMove(game, timeDue));
		
		moves.put(oldpacman.game.Constants.GHOST.BLINKY, blinkyMove);
		moves.put(oldpacman.game.Constants.GHOST.PINKY, pinkyMove);
		moves.put(oldpacman.game.Constants.GHOST.INKY, inkyMove);
		moves.put(oldpacman.game.Constants.GHOST.SUE, sueMove);
		
		return moves;
	}
}
