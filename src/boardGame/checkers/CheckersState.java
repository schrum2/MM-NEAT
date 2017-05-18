package boardGame.checkers;

import java.awt.Point;
import java.util.List;

import boardGame.BoardGameState;

public class CheckersState implements BoardGameState{

	private static final int UNDECIDED = -1;
	private static final int TIE = 0;
	private static final int PLAYER1 = 1;
	private static final int PLAYER2 = 2;

	private static final int BOARDWIDTH = 8;	
	private static final int STARTCHECKS = 12;
	
	static final int BLOCKED = -1; // Used for the unreachable parts of the Board
	static final int EMPTY = 0;

	private static final int BLACK_CHECK = 1;
	private static final int BLACK_CHECK_KING = 2;

	private static final int RED_CHECK = 3;
	private static final int RED_CHECK_KING = 4;


	private int blackChecksLeft;
	private int redChecksLeft;

	private int currentPlayer;
	
	int[][] boardState;
	
	
	/**
	 * Default Constructor
	 */
	public CheckersState(){
		boardState = newCheckBoard(); // TODO: Create a method that returns a fully initialized CheckBoard
		blackChecksLeft = STARTCHECKS;
		redChecksLeft = STARTCHECKS;
		currentPlayer = PLAYER1;
	}
	
	/**
	 * Private Constructor; takes a int[][] as a representation of a CheckerBoard
	 * 
	 * @param newBoard int[][] representing a CheckerBoard
	 */
	private CheckersState(int[][] newBoard, int currentPlay){
		// Checks if the Dimensions of the newBoard are correct
		if(newBoard.length != BOARDWIDTH){
			throw new IllegalArgumentException("Incorrect Dimensions for a Checkers Board: " + newBoard.length);
		}
		for(int i = 0; i < BOARDWIDTH; i++){
			if(newBoard[i].length != BOARDWIDTH){
				throw new IllegalArgumentException("Incorrect Dimensions for a Checkers Board: " + newBoard[i].length);
			}
		}
		
		int blackChecks = 0;
		int redChecks = 0;
		
		// Checks if the Values within the newBoard are correct and counts up the number of remaining Checks
		for(int i = 0; i < BOARDWIDTH; i++){
			for(int j = 0; j < BOARDWIDTH; j++){
				if(newBoard[i][j] != BLACK_CHECK && newBoard[i][j] != BLACK_CHECK_KING && newBoard[i][j] != RED_CHECK
						&& newBoard[i][j] != RED_CHECK_KING && newBoard[i][j] != EMPTY && newBoard[i][j] != BLOCKED){
					throw new IllegalArgumentException("Incorrect Check Value for a Checkers Board at (" + i + ", " + j + "): " + newBoard[i][j]);
				}
				if(newBoard[i][j] == BLACK_CHECK || newBoard[i][j] == BLACK_CHECK_KING){
					blackChecks++;
				}
				if(newBoard[i][j] == RED_CHECK || newBoard[i][j] == RED_CHECK_KING){
					redChecks++;
				}
			}
		}
		// Survived the Tests; can now Construct the CheckersState
		boardState = newBoard;
		blackChecksLeft = blackChecks;
		redChecksLeft = redChecks;
	}
	
	/**
	 * Returns an Array of Doubles that describes this BoardGameState
	 * 
	 * @return double[] describing this BoardGameState
	 */
	@Override
	public double[] getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the Index of the winner of the Game
	 * 
	 * @return -1 if not at an endState, 0 if there's a Tie, 1 if Player 1 wins, or 2 if Player 2 wins
	 */
	public int getWinner(){
		if(endState()){
			if(blackChecksLeft > redChecksLeft){
				return PLAYER1;
			}else if(redChecksLeft > blackChecksLeft){
				return PLAYER2;
			}else{
				return TIE; // Technically impossible, but may be good to determine when it's impossible to win to end the game early.
			}
		}else{
			return UNDECIDED;
		}
	}
	
	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if one Player has no more Checks, else returns false
	 */
	@Override
	public boolean endState() {
		return blackChecksLeft == 0 || redChecksLeft == 0;
	}

	/**
	 * Moves a Check from one position in the CheckBoard to another diagonally
	 * 
	 * @param player Index of the Player whose turn it is
	 * @param moveThis Point of the Check to be moved
	 * @param moveTo Point where the selected Check will be moved to
	 * @return True if able to move the selected Check to the new space, else returns false
	 */
	public boolean moveCheck(Point moveThis, Point moveTo){
		assert moveThis.x >= 0 && moveThis.x < BOARDWIDTH;
		assert moveThis.y >= 0 && moveThis.y < BOARDWIDTH;
		assert moveTo.x >= 0 && moveTo.x < BOARDWIDTH;
		assert moveTo.y >= 0 && moveTo.y < BOARDWIDTH;
		
		if(currentPlayer == PLAYER1){
			if(boardState[(int) moveThis.x][(int) moveThis.y] != BLACK_CHECK && boardState[(int) moveThis.x][(int) moveThis.y] != BLACK_CHECK_KING){ // Player 1 attempted to move something that wasn't a Black Check
				return false;
			}
		}else{
			if(boardState[(int) moveThis.x][(int) moveThis.y] != RED_CHECK && boardState[(int) moveThis.x][(int) moveThis.y] != RED_CHECK_KING){ // Player 2 attempted to move something that wasn't a Red Check
				return false;
			}
		}
		
		
		return false;
	}
	
	public String toString(){
		String result = " _ _ _ _ _ _ _ _ ";
		for(int i = 0; i < BOARDWIDTH; i++){
			result += "\n|";
			for(int j = 0; j < BOARDWIDTH; j++){
				if(boardState[i][j] == BLOCKED){
					result += ".";					
				}else if(boardState[i][j] == EMPTY){
					result += " ";					
				}else if(boardState[i][j] == BLACK_CHECK){
					result += "b";									
				}else if(boardState[i][j] == BLACK_CHECK_KING){
					result += "B";									
				}else if(boardState[i][j] == RED_CHECK){
					result += "r";									
				}else if(boardState[i][j] == RED_CHECK_KING){
					result += "R";									
				}
				result += "|";
			}
			result += "\n _ _ _ _ _ _ _ _";
		}
		
		return result;
	}
	
	/**
	 * Makes and returns a BoardGameState that is a duplicate of this BoardGameState
	 * 
	 * @return BoardGameState that is a duplicate of this BoardGameState
	 */
	@Override
	public CheckersState copy() {
		CheckersState temp = new CheckersState(boardState, currentPlayer);
		return temp;
	}

	/**
	 * Creates a new CheckBoard
	 * 
	 * @return int[][] representing the starting positions of a new CheckBoard
	 */
	private int[][] newCheckBoard(){
		int[][] temp = new int[BOARDWIDTH][BOARDWIDTH];
		
		int black = STARTCHECKS;
		int empty = BOARDWIDTH; // May create new Variable; turns out the number of empty Spaces at the start is 8, same as the Board Width
		int red = STARTCHECKS;
		// May be cleaner way to do this; simple checkBoard creator for now.
		for(int i = 0; i < BOARDWIDTH; i++){
			
			for(int j = 0; j < BOARDWIDTH; j++){
				
				if(i % 2 == 0){ // Even Rows of the Board
					if(j % 2 == 0){ // Even Columns of the Board
						temp[i][j] = BLOCKED;
					}else{ // Odd Columns of the Board
						if(black > 0){
							temp[i][j] = BLACK_CHECK;
							black--;
						}else if(empty > 0){
							temp[i][j] = EMPTY;
							empty--;
						}else if(red > 0){
							temp[i][j] = RED_CHECK;
							red--;
						}
					}
				}else{ // Odd Rows of the Board
					if(j % 2 == 0){ // Even Columns of the Board
						if(black > 0){
							temp[i][j] = BLACK_CHECK;
							black--;
						}else if(empty > 0){
							temp[i][j] = EMPTY;
							empty--;
						}else if(red > 0){
							temp[i][j] = RED_CHECK;
							red--;
						}
					}else{ // Odd Columns of the Board
						temp[i][j] = BLOCKED;						
					}					
				}
			}
		}
		
		return temp;
	}

	@Override
	public List<BoardGameState> possibleBoardGameStates(BoardGameState currentState) {
		// TODO Auto-generated method stub
		return null;
	}
}
