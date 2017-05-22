package boardGame.checkers;

import java.awt.Point;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class CheckersPlayer implements BoardGamePlayer{

	public Point selectMove(BoardGameState current) {
		return null;
	}
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		CheckersState state = (CheckersState) current.copy(); // Copies the current BoardGameState
		Point moveThis = selectMove(state);
		Point moveTo = selectMove(state);
		state.moveCheck(moveThis, moveTo);

		return state;
	}

}