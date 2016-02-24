package pacman.controllers.examples;

import java.util.EnumMap;
import pacman.controllers.NewGhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/**
 * The Class Legacy.
 */
public class DeterministicLegacy extends NewGhostController {

    EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    MOVE[] moves = MOVE.values();

    @Override
    public void reset() {
        super.reset();
        myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
        moves = MOVE.values();
    }

    public DeterministicLegacy() {
        myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
        moves = MOVE.values();
    }

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int targetNode = game.getPacmanCurrentNodeIndex();

        if (game.doesGhostRequireAction(GHOST.BLINKY)) {
            myMoves.put(GHOST.BLINKY,
                    game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, game.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));//
        }
        if (game.doesGhostRequireAction(GHOST.INKY)) {
            myMoves.put(GHOST.INKY,
                    game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, game.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));//
        }
        if (game.doesGhostRequireAction(GHOST.PINKY)) {
            myMoves.put(GHOST.PINKY,
                    game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, game.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));//
        }
        if (game.doesGhostRequireAction(GHOST.SUE)) {
            myMoves.put(GHOST.SUE,
                    game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.SUE), targetNode, game.getGhostLastMoveMade(GHOST.SUE), DM.PATH));//
        }
        return myMoves;
    }
}