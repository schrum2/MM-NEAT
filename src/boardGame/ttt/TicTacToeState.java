package boardGame.ttt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boardGame.BoardGameState;
import javafx.util.Pair;

public class TicTacToeState implements BoardGameState{
	
	// Package private: other classes inside this class can access it
	int[][] boardState;
	
	private int nextPlayer;
	private int winner;
	
	public static final int TIE = -1;
	
	
	public static final int EMPTY = 0;
	public static final int X = 1;
	public static final int O = 2;
	public static final int BOARD_WIDTH = 3;
	
	/**
	 * Default Constructor; creates a representation of an empty Tic-Tac-Toe Board
	 */
	public TicTacToeState(){
		boardState = new int[BOARD_WIDTH][BOARD_WIDTH];
		nextPlayer = X;
		winner = EMPTY;
	}
	
	/**
	 * Private Constructor that allows a User to create a Tic-Tac-Toe Board with a specific configuration
	 * 
	 * @param newBoard Should be an int[3][3] with values of 0, 1, or 2 only.
	 */
	private TicTacToeState(int[][] newBoard){
		// Checks if the newBoard has the correct Dimensions
		if(newBoard.length != BOARD_WIDTH || newBoard[0].length != BOARD_WIDTH || newBoard[1].length != BOARD_WIDTH || newBoard[2].length != BOARD_WIDTH){
			throw new IllegalArgumentException("Incorrect Dimensions for a Tic-Tac-Toe Board.");
		}else{
			// Checks if newBoard has correct Markings
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					if(newBoard[i][j] != 0 || newBoard[i][j] != 1 || newBoard[i][j] != 2){
						throw new IllegalArgumentException("Incorrect Markings for a Tic-Tac-Toe Board: " + newBoard[i][j] + ". Needs to be 0 (Empty), 1 (X), or 2 (O) at (" + i + ", " + j + ").");					
					}
				}
			}
			// Correct Board Dimensions and Markings
			boardState = newBoard;
		}
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
				if(boardState[i][j] == 0){
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
	public boolean fillX(Point space){
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
	public boolean fillO(Point space){
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
			return result;
		} else if(nextPlayer == O) {
			boolean result = fillO(space);
			nextPlayer = X;
			return result;
		}
		else throw new IllegalArgumentException("Can only fill with X or O, not " + nextPlayer);
	}
	
	/**
	 * Returns an Array of Doubles that describes the current BoardState
	 * 
	 * @return double[] that describes the current BoardState
	 */
	@Override
	public double[] getDescriptor() {
		// TODO: Figure out how to Implement this...
		return null;
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
		// First checks for any 3-in-a-Rows
		boolean over = false;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
						
			if(boardState[i][0] == boardState[i][1] && boardState[i][1] == boardState[i][2]  && boardState[i][2] != EMPTY){ // Checks each Row for a 3-in-a-Row
				over = true;
				winner = boardState[i][0];
			}
			
			if(boardState[0][i] == boardState[1][i] && boardState[1][i] == boardState[2][i]  && boardState[2][i] != EMPTY){ // Checks each Column for a 3-in-a-Row
				over = true;
				winner = boardState[0][i];
			}
		}
		
		if(boardState[0][0] == boardState[1][1] && boardState[1][1] == boardState[2][2]  && boardState[2][2] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row
			over = true;
			winner = boardState[0][0];
		}
		if(boardState[0][2] == boardState[1][1] && boardState[1][1] == boardState[2][0]  && boardState[2][0] != EMPTY){ // Checks the Diagonal for a 3-in-a-Row
			over = true;
			winner = boardState[0][2];
		}
		
		if(over){
			//System.out.println("Player " + winner + " Wins!");
			//printState();
			return over;
		}
		
		// Then checks for Filled Board (now unable to play)
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == EMPTY){ // There is a Space left to play; game is not over
					return false;
				}
			}
		}
		
		winner = TIE;
		//System.out.println("Board Filled; GAME OVER");
		return true; // No 3-in-a-Rows nor any Space left to play; game is over
	}

	/**
	 * Returns the Index of the winning Player
	 * 
	 * @return -1 if there is a tie, 0 if the game isn't over, 1 if Player 1 wins, or 2 if Player 2 wins 
	 */
	public int getWinner(){
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
		TicTacToeState temp = new TicTacToeState(boardState);
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
			TicTacToeState tempState = (TicTacToeState) currentState.copy();
			tempState.fill(p);
			returnStates.add(tempState);
		}
		
		return returnStates;
	}

}
