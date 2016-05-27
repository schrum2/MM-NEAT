/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.utexas.cs.nn.tasks.mspacman.agentcontroller.ghosts;

import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.mspacman.facades.GameFacade;
import edu.utexas.cs.nn.tasks.mspacman.sensors.ghosts.GhostControllerInputOutputMediator;
import java.util.EnumMap;
import pacman.controllers.NewGhostController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

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
			if (gf.newG.doesGhostRequireAction(ghost)) {
				int d = getDirection(gf, ghost);
				MOVE m = GameFacade.indexToMove(d);
				myMoves.put(ghost, m);
			}
		}

		return myMoves;
	}

	public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue) {
		return getMove(new GameFacade(game), timeDue);
	}

	public abstract int getDirection(GameFacade gf, GHOST ghost);
}
