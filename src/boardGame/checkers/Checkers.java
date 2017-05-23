package boardGame.checkers;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGame;

public class Checkers implements BoardGame<BoardGameState>{

	private int currentPlayer; // Used to keep track of whose turn it is
	private CheckersState board;
	
	public Checkers(){
		currentPlayer = 0;
		board = new CheckersState();
	}
	
	/**
	 * Returns the number of Players for Checkers
	 * 
	 * @return 2, the number of Players for Checkers
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the CheckersState has reached an end State, else returns false
	 */
	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	/**
	 * Returns the Index of the winner of the Game
	 * 
	 * @return List<Integer> of the Index of the winner
	 */
	@Override
	public List<Integer> getWinners() {
		List<Integer> temp = new ArrayList<Integer>(board.getWinner());
		return temp;
	}

	public CheckersState getState(){
		return board.copy();
	}
	
	/**
	 * Updates the BoardGameState based on the actions of a given Player
	 * 
	 * @param bgp BoardGamePlayer making the action to update the BoardGameState
	 */
	@Override
	public void move(BoardGamePlayer<BoardGameState> bgp) {
		bgp.takeAction(board);
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
		}

	/**
	 * Returns the Index of the Player whose Turn it is
	 * 
	 * @return Index of the Player whose Turn it is
	 */
	@Override
	public int getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Returns a String containing the name of the Game, "Checkers"
	 * 
	 * @return String containing "Checkers"
	 */
	@Override
	public String getName() {
		return "Checkers";
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
		board = new CheckersState();
	}

}
