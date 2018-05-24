package edu.southwestern.tasks.popacman.controllers;

import java.util.EnumMap;

import oldpacman.controllers.Controller;
import oldpacman.game.Constants;
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
	
	public EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> getMove(pacman.game.Game game, long timeDue) {

		EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE> moves = new EnumMap<oldpacman.game.Constants.GHOST, oldpacman.game.Constants.MOVE>();
		
		BLINKY.getMove(game, timeDue);
		PINKY.getMove(game, timeDue);
		INKY.getMove(game, timeDue);
		SUE.getMove(game, timeDue);
		
		return null;
	}
}
