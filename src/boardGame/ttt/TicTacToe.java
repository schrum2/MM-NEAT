package boardGame.ttt;

import java.awt.Point;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class TicTacToe implements BoardGame{

	private int currentPlayer; // Used to keep track of whose turn it is
	private TicTacToePlayer[] players; // Used to store the two BoardGamePlayers
	private TicTacToeState board;
	
	/**
	 * Default Constructor
	 */
	public TicTacToe(){
		currentPlayer = 0;
		players = new TicTacToePlayer[]{new TicTacToePlayerHuman(TicTacToeState.X), new TicTacToePlayerRandom(TicTacToeState.O)};
		board = new TicTacToeState();
	}
	
	public TicTacToe(TicTacToePlayer player1){
		currentPlayer = 0;
		players = new TicTacToePlayer[]{player1, new TicTacToePlayerRandom(TicTacToeState.O)};
		board = new TicTacToeState();
	}
	
	public TicTacToe(TicTacToePlayer player1, TicTacToePlayer player2){
		currentPlayer = 0;
		players = new TicTacToePlayer[]{player1, player2};
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

	public void move() { 
		players[currentPlayer].takeAction(board);
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
	}

	@Override
	public TicTacToePlayer getCurrentPlayer() {
		return players[currentPlayer];
	}

	@Override
	public String getName() {
		return "Tic-Tac-Toe";
	}
	
	public String toString() {
		return this.board.toString();
	}

}