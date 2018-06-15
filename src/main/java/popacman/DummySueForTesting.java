package popacman;

import pacman.controllers.IndividualGhostController;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

/**
 * This Ghost will move in the direction you specifically set. Used for 
 * JUnit tests.
 * @author Will Price
 *
 */
public class DummySueForTesting extends IndividualGhostController {

	MOVE currentMove = pacman.game.Constants.MOVE.NEUTRAL;
	
	public DummySueForTesting(GHOST ghost) {
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
