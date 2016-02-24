package pacman.controllers.examples;

import java.util.EnumMap;
import pacman.controllers.NewGhostController;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * The Class Legacy.
 */
public class ChaseFleeGhosts extends NewGhostController {

    @Override
    public void reset() {
        super.reset();
        myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
        moves = MOVE.values();
    }
    EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
    MOVE[] moves = MOVE.values();

    /*
     * (non-Javadoc) @see
     * pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
        myMoves.clear();

        int targetNode = game.getPacmanCurrentNodeIndex();

        if (game.doesGhostRequireAction(GHOST.BLINKY)) {
            if (game.isGhostEdible(GHOST.BLINKY)) {
                flee(myMoves, GHOST.BLINKY, game);
            } else {
                myMoves.put(GHOST.BLINKY,
                        game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode, game.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
            }
        }

        if (game.doesGhostRequireAction(GHOST.INKY)) {
            if (game.isGhostEdible(GHOST.INKY)) {
                flee(myMoves, GHOST.INKY, game);
            } else {
                myMoves.put(GHOST.INKY,
                        game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.INKY), targetNode, game.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
            }
        }

        if (game.doesGhostRequireAction(GHOST.PINKY)) {
            if (game.isGhostEdible(GHOST.PINKY)) {
                flee(myMoves, GHOST.PINKY, game);
            } else {
                myMoves.put(GHOST.PINKY,
                        game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode, game.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));
            }
        }

        if (game.doesGhostRequireAction(GHOST.SUE)) {
            myMoves.put(GHOST.SUE, moves[game.rnd.nextInt(moves.length)]);
        }

        return myMoves;
    }

    private void flee(EnumMap<GHOST, MOVE> myMoves, GHOST ghost, Game game) {
        myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
                game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH));

    }
}