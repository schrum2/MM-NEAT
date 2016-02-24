package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class RandomPacMan.
 */
public final class RandomPacMan extends Controller<MOVE> {

    private MOVE[] allMoves = MOVE.values();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        return allMoves[game.rnd.nextInt(allMoves.length)];
    }
}