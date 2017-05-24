package boardGame.checkers;

import java.awt.Point;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class CheckersState extends TwoDimensionalBoardGameState {

	private static final int TIE = 0;
	private static final int PLAYER1 = 1; // Player 1 controls Black Checks, at the top of the Board
	private static final int PLAYER2 = 2; // Player 2 controls Red Checks, at the bottom of the Board

	private static final int BOARD_WIDTH = 8;	
	private static final int STARTCHECKS = 12;
	
	static final int EMPTY = -1;

	private static final int BLACK_CHECK = 0;
	private static final int BLACK_CHECK_KING = 1;

	private static final int RED_CHECK = 2;
	private static final int RED_CHECK_KING = 3;


	private int blackChecksLeft;
	private int redChecksLeft;

	private int currentPlayer;
	
	int[][] boardState;
	
	private List<Integer> winner;
	
	
	/**
	 * Default Constructor
	 */
	public CheckersState(){
		super(2);
	}
	
	/**
	 * Private Constructor; takes a int[][] as a representation of a CheckerBoard
	 * 
	 * @param newBoard int[][] representing a CheckerBoard
	 */
	public CheckersState(CheckersState state){
		super(state);
	}
	
	/**
	 * Returns an Array of Doubles that describes this BoardGameState
	 * 
	 * @return double[] describing this BoardGameState
	 */
	@Override
	public double[] getDescriptor() {

		double[] features = new double[BOARD_WIDTH*BOARD_WIDTH];
		int index = 0;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j ++){
				features[index++] = boardState[i][j];
			}
		}
		
		return features;
	}

	/**
	 * Returns the Index of the winner of the Game
	 * 
	 * @return -1 if not at an endState, 0 if there's a Tie, 1 if Player 1 wins, or 2 if Player 2 wins
	 */
	public List<Integer> getWinners(){
		
		if(endState()){
			if(blackChecksLeft > redChecksLeft){
				winner.add(PLAYER1);
			}else if(redChecksLeft > blackChecksLeft){
				winner.add(PLAYER2);
			}else{
				winner.add(TIE); // Technically impossible, but may be good to determine when it's impossible to win to end the game early.
			}
		}
		return winner;
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
	public boolean move(Point moveThis, Point moveTo){
		assert moveThis.getX() >= 0 && moveThis.getX() < BOARD_WIDTH;
		assert moveThis.getY() >= 0 && moveThis.getY() < BOARD_WIDTH;
		assert moveTo.getX() >= 0 && moveTo.getX() < BOARD_WIDTH;
		assert moveTo.getY() >= 0 && moveTo.getY() < BOARD_WIDTH;
		
		int thisCheck = boardState[(int) moveThis.getX()][(int) moveThis.getY()];
		int otherSpace = boardState[(int) moveTo.getX()][(int) moveTo.getY()];
		
		// Prevents the Player from moving a piece that isn't their color of Check
		if(currentPlayer == PLAYER1){
			if(thisCheck != BLACK_CHECK && thisCheck != BLACK_CHECK_KING){ // Player 1 attempted to move something that wasn't a Black Check
				System.out.println("Player 1 can't move that space; it is a(n) " + getSpaceLabel((int) moveThis.x, (int) moveThis.y) + ", not a Black Check.");
				return false;
			}
		}else{
			if(thisCheck != RED_CHECK && thisCheck != RED_CHECK_KING){ // Player 2 attempted to move something that wasn't a Red Check
				System.out.println("Player 2 can't move that space; it is a(n) " + getSpaceLabel((int) moveThis.x, (int) moveThis.y) + ", not a Red Check.");
				return false;
			}
		}
		
		// Checks if the Player can move the selected piece to the space described
		if(Math.abs(moveThis.getX() - moveTo.getX()) != 1 || Math.abs(moveThis.getY() - moveTo.getY()) != 1){ // Can only move Checks diagonally
			System.out.println("Player " + currentPlayer + " tried to move (" + moveThis.getX() + ", " + moveThis.getY() + ") non-diagonally.");
			return false;
		}else{
			if(currentPlayer == PLAYER1){ // Player 1 controls Black Checks, which are at the top of the screen
				if(thisCheck == BLACK_CHECK && (moveThis.getY() - moveTo.getY() != 1)){ // Can only move down diagonally
					return false;
				}
			}else{ // Player 2 controls Red Checks, which are at the bottom of the screen
				if(thisCheck == RED_CHECK && (moveThis.getY() - moveTo.getY() != -1)){ // Can only move up diagonally
					return false;
				}				
			}
		}

		Point endSpace = moveTo; // Used to handle Check movement after "collisions"
		boolean left = moveThis.getX() - moveTo.getX() == 1;
		
		// Handles Check collisions and "jumping";
		if((otherSpace == BLACK_CHECK || otherSpace == BLACK_CHECK_KING) && (otherSpace == RED_CHECK || otherSpace == RED_CHECK_KING)){ // Attempting to "jump" enemy piece. Possible?
			if(left){ // Going Left
				if(moveTo.getX() - 1 < 0 || moveTo.getY() + 1 > BOARD_WIDTH){ // Would go off Board; not possible to jump
					System.out.println("Would go off of the Board: (" + (moveTo.getX()-1) + ", " + (moveTo.getY()+1) + ")");
					return false;
				}else{
					endSpace = new Point((int) moveTo.getX() - 1, (int) moveTo.getY() + 1);
					if(boardState[(int) endSpace.getX()][(int) endSpace.getY()] != EMPTY){ // Unable to jump
						System.out.println("Unable to jump the piece at (" + moveTo.getX() + ", " + moveTo.getY() + "); there is a Check at (" + endSpace.getX() + ", " + endSpace.getY() + ")");
					}else{ // Valid jump; move end position of Check, and remove enemy Check
						boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY; // Removes enemy Check
					}
				}
			}else{ // Going Right
				if(moveTo.getX() + 1 < 0 || moveTo.getY() + 1 > BOARD_WIDTH){ // Would go off Board; not possible to jump
					System.out.println("Would go off of the Board: (" + (moveTo.getX()+1) + ", " + (moveTo.getY()+1) + ")");
					return false;
				}else{ // Valid jump; move end position of Check, and remove enemy Check
					endSpace = new Point((int) moveTo.getX() + 1, (int) moveTo.getY() + 1);
					if(boardState[(int) endSpace.getX()][(int) endSpace.getY()] != EMPTY){ // Unable to jump
						System.out.println("Unable to jump the piece at (" + moveTo.getX() + ", " + moveTo.getY() + "); there is a Check at (" + endSpace.getX() + ", " + endSpace.getY() + ")");
					}else{ // Valid jump; move end position of Check, and remove enemy Check
						boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY; // Removes enemy Check
					}
				}				
			}
		}else if((otherSpace == RED_CHECK || otherSpace == RED_CHECK_KING) && (otherSpace == BLACK_CHECK || otherSpace == BLACK_CHECK_KING)){ // Attempting to "jump" enemy piece. Possible?
			if(left){ // Going Left
				if(moveTo.getX() - 1 < 0 || moveTo.getY() - 1 > BOARD_WIDTH){ // Would go off Board; not possible to jump
					System.out.println("Would go off of the Board: (" + (moveTo.getX()-1) + ", " + (moveTo.getY()-1) + ")");
					return false;
				}else{ // Valid jump; move end position of Check, and remove enemy Check
					endSpace = new Point((int) moveTo.getX() - 1, (int) moveTo.getY() - 1);
					if(boardState[(int) endSpace.getX()][(int) endSpace.getY()] != EMPTY){ // Unable to jump
						System.out.println("Unable to jump the piece at (" + moveTo.getX() + ", " + moveTo.getY() + "); there is a Check at (" + endSpace.getX() + ", " + endSpace.getY() + ")");
					}else{ // Valid jump; move end position of Check, and remove enemy Check
						boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY; // Removes enemy Check
					}
				}
			}else{ // Going Right
				if(moveTo.getX() + 1 < 0 || moveTo.getY() - 1 > BOARD_WIDTH){ // Would go off Board; not possible to jump
					System.out.println("Would go off of the Board: (" + (moveTo.getX()+1) + ", " + (moveTo.getY()-1) + ")");
					return false;
				}else{ // Valid jump; move end position of Check, and remove enemy Check
					endSpace = new Point((int) moveTo.getX() + 1, (int) moveTo.getY() - 1);
					if(boardState[(int) endSpace.getX()][(int) endSpace.getY()] != EMPTY){ // Unable to jump
						System.out.println("Unable to jump the piece at (" + moveTo.getX() + ", " + moveTo.getY() + "); there is a Check at (" + endSpace.getX() + ", " + endSpace.getY() + ")");
					}else{ // Valid jump; move end position of Check, and remove enemy Check
						boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY; // Removes enemy Check
					}
				}				
			}
		}
		
		// Survived the Deadly Trials of Logic; valid move

		boardState[(int) endSpace.getX()][(int) endSpace.getY()] = thisCheck;
		boardState[(int) moveThis.getY()][(int) moveThis.getY()] = EMPTY;
		
		// Handles being "Kinged"
		if(thisCheck == BLACK_CHECK && endSpace.getY() == BOARD_WIDTH){
			boardState[(int) endSpace.getX()][(int) endSpace.getY()] = BLACK_CHECK_KING;
		}else if(thisCheck == RED_CHECK && endSpace.getY() == 0){
			boardState[(int) endSpace.getX()][(int) endSpace.getY()] = RED_CHECK_KING;
		}
		
		return true;
	}
	
	/**
	 * Returns a String representing the piece of the CheckBoard on a given set of coordinates
	 * 
	 * @param x X-Coordinate of the specified piece
	 * @param y Y-Coordinate of the specified piece
	 * @return String representing the selected piece
	 */
	private String getSpaceLabel(int x, int y) {
		int choice = boardState[x][y];
		switch(choice) {
		case BLACK_CHECK: return "Black Check";
		case BLACK_CHECK_KING: return "Black Check King";
		case RED_CHECK: return "Red Check";
		case RED_CHECK_KING: return "Red Check King";
		case EMPTY: return "Empty";
		}
		throw new IllegalArgumentException("Must choose Rock, Paper, or Scissors, not " + choice);
	}
	
	/**
	 * Makes and returns a BoardGameState that is a duplicate of this BoardGameState
	 * 
	 * @return BoardGameState that is a duplicate of this BoardGameState
	 */
	@Override
	public CheckersState copy() {
		return new CheckersState(this);
	}

	/**
	 * Creates a new CheckBoard
	 * 
	 * @return int[][] representing the starting positions of a new CheckBoard
	 */
	private int[][] newCheckBoard(){
		int[][] temp = new int[BOARD_WIDTH][BOARD_WIDTH];
		
		int black = STARTCHECKS;
		int empty = BOARD_WIDTH; // May create new Variable; turns out the number of empty Spaces at the start is 8, same as the Board Width
		int red = STARTCHECKS;
		// May be cleaner way to do this; simple checkBoard creator for now.
		for(int i = 0; i < BOARD_WIDTH; i++){
			
			for(int j = 0; j < BOARD_WIDTH; j++){
				
				if(i % 2 == 0){ // Even Rows of the Board
					if(j % 2 == 0){ // Even Columns of the Board
						temp[i][j] = EMPTY;
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
						temp[i][j] = EMPTY;						
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

	@Override
	public void setupStartingBoard() {
		boardState = newCheckBoard();
		blackChecksLeft = STARTCHECKS;
		redChecksLeft = STARTCHECKS;
		currentPlayer = PLAYER1;
		}

	@Override
	public int getBoardWidth() {
		return BOARD_WIDTH;
	}

	@Override
	public int getBoardHeight() {
		return BOARD_WIDTH;
	}

	@Override
	public char[] getPlayerSymbols() {
		return new char[]{'b', 'B', 'r', 'R'};
	}
}
