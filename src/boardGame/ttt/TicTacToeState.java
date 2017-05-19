package boardGame.ttt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGameState;

public class TicTacToeState implements BoardGameState{
	
	// Package private: other classes inside this class can access it
	int[][] boardState;
	
	private int nextPlayer;
	private List<Integer> winner;
	
	public static final int EMPTY = -1;
	public static final int X = 0;
	public static final int O = 1;
	public static final int BOARD_WIDTH = 3;
	
	/**
	 * Default Constructor; creates a representation of an empty Tic-Tac-Toe Board
	 */
	public TicTacToeState(){
		boardState = new int[BOARD_WIDTH][BOARD_WIDTH];
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				boardState[i][j] = EMPTY;
			}
		}
		nextPlayer = X;
		winner = new ArrayList<Integer>();
	}
	
	/**
	 * Private Constructor that allows a User to create a Tic-Tac-Toe Board with a specific configuration
	 * 
	 * @param newBoard Should be an int[3][3] with values of 0, 1, or 2 only.
	 */
	public TicTacToeState(int[][] oldBoard, int oldNext, List<Integer> oldWin){
		int[][] newBoard = oldBoard;
		this.nextPlayer = oldNext;
		// Checks if the newBoard has the correct Dimensions
		
		assert oldBoard.length == 3 && oldBoard[0].length == 3 && oldBoard[1].length == 3 && oldBoard[2].length == 3;
		
		// Checks if newBoard has correct Markings
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				newBoard[i][j] = oldBoard[i][j];
			}
		}
			// Correct Board Dimensions and Markings
			boardState = newBoard;
			winner = new ArrayList<>(2); 
			winner.addAll(oldWin);
	}

	/**
	 * Returns the Index of the next Player
	 * 
	 * @return Index of the next Player
	 */
	public int getNextPlayer() {
		return nextPlayer;
	}

	/**
	 * Returns the representation of the indexes of empty Spaces as Points
	 * 
	 * @return List<Point> of the empty Spaces in the Tic-Tac-Toe Board
	 */
	public List<Point> getEmptyIndex(){
		List<Point> indexes = new ArrayList<Point>();
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == EMPTY){
					indexes.add(new Point(i, j));
				}
			}
		}
		
		return indexes;
	}
	
	/**
	 * Fills an empty Space with an X
	 * 
	 * @param space Index representing the Tic-Tac-Toe Space to be filled
	 * @param playerMark The number representing an X
	 * @return True if able to place, else returns false
	 */
	private boolean fillX(Point space){
		if(boardState[(int) space.getX()][(int) space.getY()] == EMPTY){
			boardState[(int) space.getX()][(int) space.getY()] = X;
			return true;
		}else{
			System.out.println("Cannot place there: X");
			return false;
		}
	}
	
	/**
	 * Fills an empty Space with an O
	 * 
	 * @param space Index representing the Tic-Tac-Toe Space to be filled
	 * @param playerMark The number representing an O
	 * @return True if able to place, else returns false
	 */
	private boolean fillO(Point space){
		if(boardState[(int) space.getX()][(int) space.getY()] == EMPTY){
			boardState[(int) space.getX()][(int) space.getY()] = O;
			return true;
		}else{
			System.out.println("Cannot place there: O");
			return false;
		}
	}
	
	/**
	 * Fills in the selected Point on the Tic-Tac-Toe Board with the correct Marking
	 * 
	 * @param space Point representing a Space on the Tic-Tac-Toe Board
	 * @return True if the Player is able to place a Marking in the selected Space, else returns false
	 */
	public boolean fill(Point space) {
		if(nextPlayer == X) {
			boolean result = fillX(space);
			nextPlayer = O;
			checkWinner();
			return result;
		} else if(nextPlayer == O) {
			boolean result = fillO(space);
			nextPlayer = X;
			checkWinner();
			return result;
		}
		else throw new IllegalArgumentException("Can only fill with X or O, not " + nextPlayer);
	}
	
	private void checkWinner(){
		for(int i = 0; i < BOARD_WIDTH; i++){		
			if(boardState[i][0] == boardState[i][1] && boardState[i][1] == boardState[i][2]  && boardState[i][2] != EMPTY){ // Checks each Row for a 3-in-a-Row
				winner.add(boardState[i][0]);
			}else if(boardState[0][i] == boardState[1][i] && boardState[1][i] == boardState[2][i]  && boardState[2][i] != EMPTY){ // Checks each Column for a 3-in-a-Row
				winner.add(boardState[0][i]);
			}
		}
		
		if(winner.isEmpty()){
			if(boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]  && boardState[2][2] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row
				winner.add(boardState[0][0]);
			}else if(boardState[0][2] == boardState[1][1] && boardState[1][1] == boardState[2][0]  && boardState[2][0] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row			over = true;
				winner.add(boardState[0][2]);
			}
		}
		
		if(winner.isEmpty()){
			// Then checks for Filled Board (now unable to play)
			winner.add(X);
			winner.add(O);
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					if(boardState[i][j] == EMPTY){ // There is a Space left to play; game is not over
						winner.clear();
					}
				}
			}
			//System.out.println("Board Filled; GAME OVER");
		}
	}
	
	/**
	 * Returns an Array of Doubles that describes the current BoardState
	 * 
	 * @return double[] that describes the current BoardState
	 */
	@Override
	public double[] getDescriptor() {
		double[] feature = new double[9];
		int index = 0;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				feature[index++] = boardState[i][j];
			}
		}
		
		return feature;
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
	public boolean endState() {
		return !winner.isEmpty();
	}

	/**
	 * Returns the Index of the winning Player
	 * 
	 * @return ArrayList<Integer> containing the winners
	 */
	public List<Integer> getWinner(){
		return winner;
	}
	
	/**
	 * Prints out a visual representation of the TicTacToeState to the console
	 */
	public String toString(){
		String result = "-------\n";
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			result += "|";
			
			for(int j = 0; j < BOARD_WIDTH; j++){
				
				char mark;
				int space = boardState[i][j];
				
				if(space == EMPTY){
					mark = ' ';
				}else if(space == X){
					mark = 'X';
				}else{
					mark = 'O';
				}
				result += mark + "|";
			}
			
			result += "\n-------\n";
		}
		result += "\n\n";
		return result;
	}

	/**
	 * Creates and returns a copy of the current TicTacToeState
	 * 
	 * @return TicTacToeState identical to this TicTacToeState
	 */
	@Override
	public TicTacToeState copy() {
		int[][] tempBoard = new int[BOARD_WIDTH][BOARD_WIDTH];
		int tempNext = nextPlayer;
		List<Integer> tempWin = new ArrayList<Integer>(2);
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				tempBoard[i][j] = boardState[i][j];
			}
		}
				
		tempWin.addAll(winner);
		
		TicTacToeState temp = new TicTacToeState(tempBoard, tempNext, tempWin);
		return temp;
	}

	/**
	 * Returns a List of all possible Moves in a Game starting from a given State
	 * 
	 * @return List<BoardGameStates> of States possible starting from a specific BoardGameState
	 */
	@Override
	public List<BoardGameState> possibleBoardGameStates(BoardGameState currentState) {
		List<BoardGameState> returnStates = new ArrayList<BoardGameState>();
		List<Point> tempPoints = getEmptyIndex();

		for(Point p : tempPoints){
			TicTacToeState temp = (TicTacToeState) currentState.copy();
			temp.fill(p);
			returnStates.add(temp);
		}
		return returnStates;
	}

}
