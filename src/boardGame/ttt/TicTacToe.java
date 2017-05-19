package boardGame.ttt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class TicTacToe implements BoardGame<TicTacToeState> {

	private int currentPlayer; // Used to keep track of whose turn it is
	private TicTacToeState board;
	
	/**
	 * Default Constructor
	 */
	public TicTacToe(){
		currentPlayer = 0;
		board = new TicTacToeState();
	}
	
	/**
	 * Returns the number of Players for Tic-Tac-Toe
	 * 
	 * @return 2, the number of Players
	 */
	@Override
	public int getNumPlayers() {
		return 2; // TicTacToe can only ever have two Players
	}

	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the BoardState reached an end State, else returns false
	 */
	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	/**
	 * Returns a List of the winners of a given Game
	 * 
	 * @return List<Integer> of the Indexes of the game winners
	 */
	@Override
	public List<Integer> getWinners() {
		List<Integer> winner = new ArrayList<Integer>(board.getWinner());
		return winner;
	}

	/**
	 * Updates the Game based on the actions of the current Player
	 */
	public void move(BoardGamePlayer<TicTacToeState> bgp) { 
		board = bgp.takeAction(board);
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
	}

	/**
	 * Returns the Index of the current Player
	 * 
	 * @return Index of the current Player
	 */
	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Returns a String containing the name of the game, "Tic-Tac-Toe"
	 * 
	 * @return String containing "Tic-Tac-Toe"
	 */
	@Override
	public String getName() {
		return "Tic-Tac-Toe";
	}
	
	/**
	 * Returns a String containing a visual representation of the current State
	 * 
	 * @return String visually representing the current TicTacToeState
	 */
	public String toString() {
		return this.board.toString();
	}

	@Override
	public String[] getFeatureLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Resets the currentPlayer to 0 and creates a new BoardGameState to use
	 */
	@Override
	public void reset() {
		currentPlayer = 0;
		board = new TicTacToeState();
	}

}