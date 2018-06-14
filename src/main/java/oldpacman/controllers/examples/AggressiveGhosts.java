package oldpacman.controllers.examples;

import edu.southwestern.parameters.Parameters;
import oldpacman.controllers.NewGhostController;
import oldpacman.game.Game;
import oldpacman.game.Constants.DM;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

import java.util.EnumMap;

/*
 * The Class AggressiveGhosts.
 */
public final class AggressiveGhosts extends NewGhostController {

	@Override
	public void reset() {
		super.reset();
		myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
		moves = MOVE.values();
	}

	public AggressiveGhosts() {
		consistency = Parameters.parameters.doubleParameter("aggressiveGhostConsistency");
	}

	private final double consistency; // carry out intended move with this
										// probability
	private EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	private MOVE[] moves = MOVE.values();

	/*
	 * (non-Javadoc)
	 * 
	 * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
	 */
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		myMoves.clear();

		for (GHOST ghost : GHOST.values()) // for each ghost
		{
			if (game.doesGhostRequireAction(ghost)) // if it requires an action
			{
				if (game.rnd.nextFloat() < consistency) // approach/retreat from
														// the current node that
														// Ms Pac-Man is at
				{
					myMoves.put(ghost, game.getApproximateNextMoveTowardsTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH));
				} else // else take a random action
				{
					myMoves.put(ghost, moves[game.rnd.nextInt(moves.length)]);
				}
			}
		}

		return myMoves;
	}
}