package boardGame;

import java.util.List;

import boardGame.agents.BoardGamePlayer;

public abstract class TwoDimensionalBoardGame<T extends TwoDimensionalBoardGameState> implements BoardGame<T>{
	
	private T startingBoard;
	private T board;
	
	/**
	 * Default Constructor
	 */
	@SuppressWarnings("unchecked")
	public TwoDimensionalBoardGame(T startingBoard){
		this.startingBoard = startingBoard;
		board = (T) startingBoard.copy();
	}
	
	@Override
	public double[] getDescription(){
		return board.getDescriptor();
	}
	
	/**
	 * Starting board game state
	 * @return
	 */
	public T getStartingState() {
		return startingBoard;
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
		 return board.getWinners();
	}
	
	/**
	 * Updates the Game based on the actions of the current Player
	 */
	public void move(BoardGamePlayer<T> bgp) { 
		board = bgp.takeAction(board);
	}

	/**
	 * Returns the Index of the current Player
	 * 
	 * @return Index of the current Player
	 */
	@Override
	public int getCurrentPlayer() {
		return board.nextPlayer;
	}

	/**
	 * Returns a String containing a visual representation of the current State
	 * 
	 * @return String visually representing the current TicTacToeState
	 */
	public String toString() {
		return this.board.toString();
	}

	/**
	 * Resets the currentPlayer to 0 and creates a new BoardGameState to use
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void reset() {
		board = (T) startingBoard.copy();
	}

	

	/**
	 * Returns the current BoardGameState
	 * 
	 * @return The Current BoardGameState
	 */
	@Override
	public TwoDimensionalBoardGameState getCurrentState(){
		return board;
	}

}
