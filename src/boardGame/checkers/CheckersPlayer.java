package boardGame.checkers;

import java.awt.Point;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public abstract class CheckersPlayer implements BoardGamePlayer<CheckersState> {

	// Schrum: Because this method is not properly defined here, it is made abstract
	public abstract Point selectMove(BoardGameState current);
	
	@Override
	public CheckersState takeAction(CheckersState current) {
		CheckersState state = current.copy(); // Copies the current BoardGameState
		// Schrum: This double call to selectMove seems suspicious and potentially unnecessary
		Point moveThis = selectMove(state);
		Point moveTo = selectMove(state);
		state.move(moveThis, moveTo);

		return state;
	}

}