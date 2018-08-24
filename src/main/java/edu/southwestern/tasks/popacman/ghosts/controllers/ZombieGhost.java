package edu.southwestern.tasks.popacman.ghosts.controllers;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * This class is a zombie class. It has instructions passed to it via OldToNewGhostIntermediaryController via the setMove method.
 * When asked what move to make, this class will return the move last given to it by OldToNewGhostIntermediaryController.
 * @author pricew
 *
 */
public class ZombieGhost extends pacman.controllers.IndividualGhostController{

	public MOVE myMove = MOVE.UP;
	
	public ZombieGhost(GHOST ghost) {
		super(ghost);
	}

	@Override
	public MOVE getMove(Game game, long timeDue) {
		return myMove;
	}
	
	public void setMove(MOVE move) {
		myMove = move;
	}

}
