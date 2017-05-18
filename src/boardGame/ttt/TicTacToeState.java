package boardGame.ttt;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boardGame.BoardGameState;
import javafx.util.Pair;

public class TicTacToeState implements BoardGameState{
	
	private int[][] boardState;
	
	public static final int EMPTY = 0;
	public static final int X = 1;
	public static final int O = 2;
	
	/**
	 * Default Constructor; creates a representation of an empty Tic-Tac-Toe Board
	 */
	public TicTacToeState(){
		boardState = new int[3][3];
	}

	/**
	 * Returns the representation of the indexes of empty Spaces as Points
	 * 
	 * @return List<Point> of the empty Spaces in the Tic-Tac-Toe Board
	 */
	public List<Point> getEmptyIndex(){
		List<Point> indexes = new ArrayList<Point>();
		
		for(int i = 0; i < boardState.length; i++){
			for(int j = 0; j < boardState[i].length; j++){
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
	
	
	@Override
	public double[] getDescriptor() {
		// TODO Auto-generated method stub
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
		int winner = 0;
		
		for(int i = 0; i < boardState.length; i++){
						
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
		
		for(int i = 0; i < boardState.length; i++){
			for(int j = 0; j < boardState[i].length; j++){
				if(boardState[i][j] == EMPTY){ // There is a Space left to play; game is not over
					return false;
				}
			}
		}
		
		//System.out.println("Board Filled; GAME OVER");
		return true; // No 3-in-a-Rows nor any Space left to play; game is over
	}

	/**
	 * Prints out a visual representation of the TicTacToeState to the console
	 */
	public String toString(){
		String result = "-------\n";
		
		for(int i = 0; i < boardState.length; i++){
			result += "|";
			
			for(int j = 0; j < boardState[i].length; j++){
				
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
	
	
	
}
