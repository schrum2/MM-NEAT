package boardGame.ttt;

import java.awt.Point;

import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public abstract class TicTacToePlayer implements BoardGamePlayer{
	
	private int symbol;
	
	public TicTacToePlayer(int symbol) {
		assert symbol == TicTacToeState.X || symbol == TicTacToeState.O;
		this.symbol = symbol;
	}
	
	public abstract Point selectMove(TicTacToeState current);

	@Override
	public BoardGameState takeAction(BoardGameState current) {
		TicTacToeState state = (TicTacToeState) current;
		Point p = selectMove(state);
		if(state.boardState[p.x][p.y] != TicTacToeState.EMPTY) throw new IllegalArgumentException("Cannot move to occupied space");
		// TODO: Rather than modify the state, copy then state then modify that
		state.fill(symbol, p);
		return state;
	}
	
}
