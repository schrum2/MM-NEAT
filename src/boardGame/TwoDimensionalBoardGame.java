package boardGame;

import java.util.List;

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
		 return board.getWinner();
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

	@Override
	public String[] getFeatureLabels() {
		String[] result = new String[startingBoard.getBoardHeight() * startingBoard.getBoardWidth()];
		for(int j = 0; j < startingBoard.getBoardHeight(); j++) {
			for(int i = 0; i < startingBoard.getBoardWidth(); i++) {
				result[j * startingBoard.getBoardWidth() + i] = "Space ("+i+","+j+")";
			}
		}
		return result;
	}


}
