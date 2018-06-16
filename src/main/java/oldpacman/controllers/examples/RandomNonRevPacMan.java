package oldpacman.controllers.examples;

import oldpacman.controllers.Controller;
import oldpacman.game.Game;
import oldpacman.game.Constants.MOVE;

/*
 * The Class RandomNonRevPacMan.
 */
public final class RandomNonRevPacMan extends Controller<MOVE> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public MOVE getMove(Game game, long timeDue) {
		MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade()); // set
																														// flag
																														// as
																														// false
																														// to
																														// prevent
																														// reversals

		return possibleMoves[game.rnd.nextInt(possibleMoves.length)];
	}
}