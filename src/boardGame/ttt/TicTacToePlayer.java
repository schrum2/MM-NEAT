package boardGame.ttt;

import java.awt.Point;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class TicTacToePlayer implements BoardGamePlayer{
	
	/**
	 * Default Constructor
	 */
	public TicTacToePlayer() {
	}
	
	/**
	 * Allows a Player to select a Move based on their internal Logic
	 * 
	 * @param current TicTacToeState currently being played
	 * @return Point representing the Move a Player took
	 */
	public abstract Point selectMove(TicTacToeState current);

	/**
	 * Uses the Move taken by a Player to fill the Board
	 * 
	 * @param current BoardGameState being used
	 * @return BoardGameState affected by the Move a Player took
	 */
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		TicTacToeState state = (TicTacToeState) current.copy(); // Copies the current BoardGameState
		Point p = selectMove(state);
		if(state.boardState[p.x][p.y] != TicTacToeState.EMPTY) throw new IllegalArgumentException("Cannot move to occupied space");


		state.fill(p);
		return state;
	}
	
}
