package oldpacman.entries.pacman;

import oldpacman.controllers.Controller;
import oldpacman.game.Game;
import oldpacman.game.Constants.MOVE;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE> {

	private MOVE myMove = MOVE.NEUTRAL;

	public MOVE getMove(Game game, long timeDue) {
		// Place your game logic here to play the game as Ms Pac-Man

		return myMove;
	}
}