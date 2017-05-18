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
		Point p = selectMove(state);
		if(state.boardState[p.x][p.y] != CheckersState.EMPTY) throw new IllegalArgumentException("Cannot move to occupied space");
		// TODO: Create actual update here; need to create moveCheck() in CheckersState first
		return state;
	}

}