package boardGame.ttt;

import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class TicTacToe implements BoardGame{

	private int currentPlayer; // Used to keep track of whose turn it is
	private TicTacToeState board;
	
	/**
	 * Default Constructor
	 */
	public TicTacToe(){
		currentPlayer = 0;
		board = new TicTacToeState();
	}
		
	@Override
	public int getNumPlayers() {
		return 2; // TicTacToe can only ever have two Players
	}

	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	@Override
	public List<Integer> getWinners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BoardGameState> possibleBoardGameStates(int player, BoardGameState currentState) {
		// TODO Auto-generated method stub
		return null;
	}

	public void move(BoardGamePlayer bgp) { 
		bgp.takeAction(board);
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
	}

	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public String getName() {
		return "Tic-Tac-Toe";
	}
	
	public String toString() {
		return this.board.toString();
	}

}