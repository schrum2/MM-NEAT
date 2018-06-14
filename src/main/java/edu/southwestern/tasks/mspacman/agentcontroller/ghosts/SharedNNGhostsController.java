package edu.southwestern.tasks.mspacman.agentcontroller.ghosts;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.mspacman.facades.GameFacade;
import edu.southwestern.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import oldpacman.controllers.NewGhostController;
import oldpacman.game.Game;
import oldpacman.game.Constants.GHOST;
import oldpacman.game.Constants.MOVE;

import java.util.EnumMap;

/**
 *
 * @author Jacob Schrum
 */
public abstract class SharedNNGhostsController extends NewGhostController {

	protected Network nn;
	public GhostControllerInputOutputMediator inputMediator;
	// No such thing yet
	// protected GhostModeSelector ms = null;

	public SharedNNGhostsController(Network n) {
		this.nn = n;
		this.inputMediator = MMNEAT.ghostsInputOutputMediator;
	}

	EnumMap<GHOST, MOVE> myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
	MOVE[] moves = MOVE.values();

	@Override
	public void reset() {
		super.reset();
		myMoves = new EnumMap<GHOST, MOVE>(GHOST.class);
		moves = MOVE.values();
	}

	public EnumMap<GHOST, MOVE> getMove(GameFacade gf, long timeDue) {
		myMoves.clear();

		for (GHOST ghost : GHOST.values()) {
			if (gf.oldG.doesGhostRequireAction(ghost)) {
				int d = getDirection(gf, ghost);
				MOVE m = GameFacade.indexToMove(d);
				myMoves.put(ghost, m);
			}
		}

		return myMoves;
	}

        @Override
	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		return getMove(new GameFacade(game), timeDue);
	}

	public abstract int getDirection(GameFacade gf, GHOST ghost);
}
