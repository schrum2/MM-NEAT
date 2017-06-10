package boardGame.ttt;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class TicTacToeState extends TwoDimensionalBoardGameState {
	

	// Really only used in one place now. Probably ok to remove X and O.
	public static final int X = 0;
	public static final int O = 1;
	public static final int NUMBER_OF_PLAYERS = 2;
	public static final int BOARD_WIDTH = 3;
	
	/**
	 * Default Constructor; creates a representation of an empty Tic-Tac-Toe Board
	 */
	public TicTacToeState(){
		super(NUMBER_OF_PLAYERS); // Always two players for TTT
	}
	
	/**
	 * Constructor that allows a User to create a Tic-Tac-Toe Board with a specific configuration
	 * 
	 * @param newBoard Should be an int[3][3] with values of 0, 1, or 2 only.
	 */
	public TicTacToeState(TicTacToeState state){
		super(state);
	}
	
	/**
	 * Constructor solely used for testing
	 * 
	 * @param board
	 * @param numPlay
	 * @param player
	 * @param win
	 */
	TicTacToeState(int[][] board, int player, List<Integer> win){
		super(board, NUMBER_OF_PLAYERS, player, win);
	}
	
	public int getNumPlayers(){
		return NUMBER_OF_PLAYERS;
	}
	
	/**
	 * Fills in the selected Point on the Tic-Tac-Toe Board with the correct Marking
	 * 
	 * @param space Point representing a Space on the Tic-Tac-Toe Board
	 * @return True if the Player is able to place a Marking in the selected Space, else returns false
	 */
	@Override
	public boolean moveSinglePoint(Point space) {
		if(placePlayerPiece(nextPlayer, space)) {
			nextPlayer = (nextPlayer + 1) % numPlayers;
			checkWinner();
			return true;
		} else {
			return false;
		}
	}
	
	private void checkWinner(){
		for(int i = 0; i < BOARD_WIDTH; i++){		
			if(boardState[i][0] == boardState[i][1] && boardState[i][1] == boardState[i][2]  && boardState[i][2] != EMPTY){ // Checks each Row for a 3-in-a-Row
				winners.add(boardState[i][0]);
			}else if(boardState[0][i] == boardState[1][i] && boardState[1][i] == boardState[2][i]  && boardState[2][i] != EMPTY){ // Checks each Column for a 3-in-a-Row
				winners.add(boardState[0][i]);
			}
		}
		
		if(winners.isEmpty()){
			if((boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]) && boardState[0][0] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row
				winners.add(boardState[0][0]);
			}else if(boardState[0][2] == boardState[1][1] && boardState[1][1] == boardState[2][0]  && boardState[2][0] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row			over = true;
				winners.add(boardState[0][2]);
			}
		}
		
		if(winners.isEmpty()){
			// Then checks for Filled Board (now unable to play)
			winners.add(X);
			winners.add(O);
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					if(boardState[i][j] == EMPTY){ // There is a Space left to play; game is not over
						winners.clear();
					}
				}
			}
			//System.out.println("Board Filled; GAME OVER");
		}
	}

	/** Visual Representation of the Tic-Tac-Toe Board
	 * 0 | 0|1|2
	 * - |-------
	 * 1 | 0|1|2
	 * - |-------
	 * 2 | 0|1|2
	 */
	
	/**
	 * Returns true if the game of Tic-Tac-Toe is over, else returns false
	 * 
	 * @return True if there is a 3-in-a-Row filled with Non-Zeros or if the entire Board is filled with Non-Zeros, else returns false
	 */
	@Override
	public boolean endState() { // Consider moving to 2DBoardGameState
		checkWinner();
		return !winners.isEmpty();
	}

	/**
	 * Creates and returns a copy of the current TicTacToeState
	 * 
	 * @return TicTacToeState identical to this TicTacToeState
	 */
	@SuppressWarnings("unchecked")
	@Override
	public TicTacToeState copy() {
		return new TicTacToeState(this);
	}

	/**
	 * Returns a List of all possible Moves in a Game starting from a given State
	 * 
	 * @return List<BoardGameStates> of States possible starting from a specific BoardGameState
	 */
	@Override
	public Set<BoardGameState> possibleBoardGameStates(BoardGameState currentState) {
		Set<BoardGameState> returnStates = new HashSet<BoardGameState>();
		List<Point> tempPoints = getEmptyIndex();

		for(Point p : tempPoints){
			TicTacToeState temp = (TicTacToeState) currentState.copy();
			temp.moveSinglePoint(p);
//			System.out.println(temp);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
			returnStates.add(temp);
		}
		return returnStates;
	}

	@Override
	public void setupStartingBoard() {
		// Do nothing to the already empty board
	}

	@Override
	public int getBoardWidth() {
		return BOARD_WIDTH;
	}

	@Override
	public int getBoardHeight() {
		return BOARD_WIDTH;
	}
	
	public char[] getPlayerSymbols() {
		return new char[]{'X','O'};
	}

	@Override
	public Color[] getPlayerColors() {
		return new Color[]{Color.blue, Color.red};
	}

	/**
	 * Unused by this BoardGame; always returns false
	 */
	@Override
	public boolean moveDoublePoint(Point goTo, Point moveTo) {
		return false;
	}

	/**
	 * TicTacToe only requires one Point to move; always returns true
	 */
	@Override
	public boolean moveOnePiece() {
		return true;
	}
}
