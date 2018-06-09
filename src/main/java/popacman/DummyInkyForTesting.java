package popacman;

import pacman.controllers.IndividualGhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * This Ghost will move in the direction you specifically set. Used for 
 * JUnit tests.
 * @author Will Price
 *
 */
public class DummyInkyForTesting extends IndividualGhostController {

	MOVE currentMove = pacman.game.Constants.MOVE.NEUTRAL;
	
	public DummyInkyForTesting(GHOST ghost) {
		super(ghost);
	}
	
	public void setMove(MOVE move) {
		this.currentMove = move;
	}


	@Override
	public MOVE getMove(Game game, long timeDue) {
		return getMove();
	}
	
	public MOVE getMove() {
		return currentMove;
	}

}
