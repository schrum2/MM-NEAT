package oldpacman.controllers.examples;

import edu.southwestern.util.random.RandomNumbers;
import oldpacman.controllers.Controller;
import oldpacman.game.Game;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

import static oldpacman.game.Constants.*;

import java.util.EnumMap;
import java.util.Random;

/**
 * The Class AttractRepelGhosts.
 */
public final class PansyGhosts extends Controller<EnumMap<GHOST, MOVE>> {

	private final static float CONSISTENCY = 1.0f; // carry out intended move
													// with this probability
	private Random rnd = new Random(RandomNumbers.randomGenerator.nextInt());
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
				if (rnd.nextFloat() < CONSISTENCY) // approach/retreat from the
													// current node that Ms
													// Pac-Man is at
				{
					myMoves.put(ghost, game.getApproximateNextMoveAwayFromTarget(game.getGhostCurrentNodeIndex(ghost),
							game.getPacmanCurrentNodeIndex(), game.getGhostLastMoveMade(ghost), DM.PATH));
				} else // else take a random action
				{
					myMoves.put(ghost, moves[rnd.nextInt(moves.length)]);
				}
			}
		}

		return myMoves;
	}
}