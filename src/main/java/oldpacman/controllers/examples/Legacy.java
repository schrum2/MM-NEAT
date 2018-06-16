package oldpacman.controllers.examples;

import java.util.EnumMap;

import oldpacman.controllers.NewGhostController;
import oldpacman.game.Game;
import oldpacman.game.Constants.DM;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

/*
 * The Class Legacy.
 */
public class Legacy extends NewGhostController {

	@Override
	public void reset() {
		super.reset();
		myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
		moves = MOVE.values();
	}

	EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	MOVE[] moves = MOVE.values();

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();

		int targetNode = game.getPacmanCurrentNodeIndex();

		if (game.doesGhostRequireAction(GHOST.BLINKY)) {
			myMoves.put(GHOST.BLINKY,
					game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.BLINKY), targetNode,
							game.getGhostLastMoveMade(GHOST.BLINKY), DM.PATH));
		}

		if (game.doesGhostRequireAction(GHOST.INKY)) {
			myMoves.put(GHOST.INKY, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.INKY),
					targetNode, game.getGhostLastMoveMade(GHOST.INKY), DM.MANHATTAN));
		}

		if (game.doesGhostRequireAction(GHOST.PINKY)) {
			myMoves.put(GHOST.PINKY,
					game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(GHOST.PINKY), targetNode,
							game.getGhostLastMoveMade(GHOST.PINKY), DM.EUCLID));
		}

		if (game.doesGhostRequireAction(GHOST.SUE)) {
			myMoves.put(GHOST.SUE, moves[game.rnd.nextInt(moves.length)]);
		}

		return myMoves;
	}
}