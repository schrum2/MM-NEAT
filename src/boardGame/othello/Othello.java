package boardGame.othello;

import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGame;

public class Othello extends TwoDimensionalBoardGame implements BoardGame<BoardGameState>{
	
	private int currentPlayer; // Used to keep track of whose turn it is
	private OthelloState board;
	private final int NUMBER_OF_FEATURES = 64;
	private final int BOARD_WIDTH = 8;
	
	/**
	 * Default Constructor
	 */
	public Othello(){
		currentPlayer = 0;
		board = new OthelloState();
	}
	
	/**
	 * Returns the number of Players for Othello
	 * 
	 * @return 2, the number of Players for Othello
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the GameState has reached an End State, else returns false
	 */
	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	/**
	 * Returns the List of winners of the Game
	 * 
	 * @return List<Integer> containing the Indexes of the winners of the Game
	 */
	@Override
	public List<Integer> getWinners() {
		return board.getWinner();
	}

	/**
	 * Allows a given Player to make a Move on the current BoardGameState
	 */
	@Override
	public void move(BoardGamePlayer<BoardGameState> bgp) {
		bgp.takeAction(board);
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
	 * Returns a String containing the name of the Game
	 * 
	 * @return String containing "Othello/Reversi"
	 */
	@Override
	public String getName() {
		return "Othello/Reversi";
	}

	/**
	 * Returns an Array of Strings that contain the Feature Labels for this Game
	 * 
	 * @return String[] containing the Feature Labels of this Game
	 */
	@Override
	public String[] getFeatureLabels() {
		String[] feature = new String[NUMBER_OF_FEATURES];
		int index = 0;
		char column = 'A';
		int row = 1;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				feature[index++] = "" + column + row;
				row++;
			}
			column++;
		}
		
		return feature;
	}

	/**
	 * Resets the BoardGameState
	 */
	@Override
	public void reset() {
		currentPlayer = 0;
		board = new OthelloState();
	}


	public String toString() {
		return board.toString();
	}
}
