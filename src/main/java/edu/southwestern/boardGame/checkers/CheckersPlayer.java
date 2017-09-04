package edu.southwestern.boardGame.checkers;

import java.awt.Point;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;

public abstract class CheckersPlayer implements BoardGamePlayer<CheckersState> {

	// TODO: Is this Player used anywhere? May be good to check, and then delete if it's not used
	// This is the only Board Game with its own Player; I don't think it's actually being used
	
	// Schrum: Because this method is not properly defined here, it is made abstract
	public abstract Point selectMove(BoardGameState current);
	
	@Override
	public CheckersState takeAction(CheckersState current) {
		CheckersState state = current.copy(); // Copies the current BoardGameState
		// Schrum: This double call to selectMove seems suspicious and potentially unnecessary
		Point moveThis = selectMove(state);
		Point moveTo = selectMove(state);
		state.moveDoublePoint(moveThis, moveTo);

		return state;
	}

}