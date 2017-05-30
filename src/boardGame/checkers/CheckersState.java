package boardGame.checkers;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class CheckersState extends TwoDimensionalBoardGameState {

	private static final int BOARD_WIDTH = 8;	
	private static final int STARTCHECKS = 12;
	
	private static final int BLACK_CHECK = 0; // Player 1 controls Black Checks, at the top of the Board
	private static final int BLACK_CHECK_KING = 1;

	private static final int RED_CHECK = 2; // Player 2 controls Red Checks, at the bottom of the Board
	private static final int RED_CHECK_KING = 3;
	
	private static int blackChecksLeft;
	private static int redChecksLeft;	
	
	private static Point doubleJumpCheck; // Used to keep track of a Check that can Double Jump
	
	/**
	 * Default Constructor
	 */
	public CheckersState(){
		super(2);
	}
	
	/**
	 * Alternate Constructor; takes a int[][] as a representation of a CheckerBoard
	 * 
	 * @param newBoard int[][] representing a CheckerBoard
	 */
	public CheckersState(CheckersState state){
		super(state);
	}

	/**
	 * Private Constructor solely used for Testing
	 * 
	 * @param newBoard
	 * @param player
	 * @param win
	 */
	CheckersState(int[][] newBoard, int player, ArrayList<Integer> win){
		super(newBoard, 2, player, win);
	}
	
	/**
	 * Returns the Index of the winner of the Game
	 * 
	 * @return -1 if not at an endState, 0 if there's a Tie, 1 if Player 1 wins, or 2 if Player 2 wins
	 */
	public List<Integer> getWinners(){
		
		if(endState()){
			if(blackChecksLeft > redChecksLeft){
				winners.add(BLACK_CHECK);
			}else if(redChecksLeft > blackChecksLeft){
				winners.add(RED_CHECK);
			}else{
				winners.add(BLACK_CHECK);
				winners.add(RED_CHECK);
			}
		}
		return winners;
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
	 * Checks if a Move is valid
	 * 
	 * @param moveThis Point on the Board of the Check being Moved
	 * @param moveTo Point on the Board being Moved to
	 * @return True if the Move is valid, else returns false
	 */
	private boolean checkMovement(Point moveThis, Point moveTo){
		
		int xOffset = (int) Math.abs(moveThis.getX() - moveTo.getX());
		int yOffset = (int) Math.abs(moveThis.getY() - moveTo.getY());
		
		
		if(!(xOffset == 1 && yOffset == 1)) return false; // Both the X-Offset and the Y-Offset must be exactly 1; Jumping selects the Enemy Check being Jumped
		
		if(doubleJumpCheck != null){ // If able, must complete a Double Jump
			if((int) moveThis.getX() != (int) doubleJumpCheck.getX() || (int) moveThis.getY() != (int) doubleJumpCheck.getY()) return false;
		}
		
		// Returns False only if unable to Move or Jump
		return (boardState[(int) moveTo.getX()][(int) moveTo.getY()] == EMPTY || ableToJump(moveThis, moveTo));
	}
	
	/**
	 * Checks if the Check is able to Jump over the specified Point
	 * 
	 * @param moveThis Point on the Board of the Check being Moved
	 * @param moveTo Point on the Board being Jumped over
	 * @return True if the Jump is valid, else returns false
	 */
	private boolean ableToJump(Point moveThis, Point moveTo){
		
		int checkX = (int) moveThis.getX();
		int checkY = (int) moveThis.getY();
		
		int otherX = (int) moveTo.getX();
		int otherY = (int) moveTo.getY();
		
		if(doubleJumpCheck != null){ // Must complete the Double Jump
			if(checkX != (int) doubleJumpCheck.getX() || checkY != (int) doubleJumpCheck.getY()) return false;
		}
		
		
		int dX = 2*checkX - 2*otherX; // Used to check Jump Space
		int dY = 2*checkY - 2*otherY; // Used to check Jump Space
		if(!isPointInBounds(new Point(dX, dY))) return false; // dX and/or dY are off the Board; impossible to Jump
		
		
		
		int jumpSpace = boardState[dX][dY];
		if(jumpSpace != EMPTY) return false; // Jump Space is not Empty; cannot Jump
		
		
		int thisCheck = boardState[checkX][checkY]; // Keeps track of the Player Check
		int otherSpace = boardState[otherX][otherY]; // Keeps track of the Space being Jumped
		
		// Jump Space is within Bounds and is Empty; must check other conditions of the Board
		
		if(thisCheck == BLACK_CHECK && dY > 0 && (otherSpace == RED_CHECK || otherSpace == RED_CHECK_KING)){ // Black Checks must Move down the screen; Y increases
			return true;
		}else if(thisCheck == RED_CHECK && dY < 0 && (otherSpace == BLACK_CHECK || otherSpace == BLACK_CHECK_KING)){ // Red Checks must Move up the screen; Y decreases
			return true;
		}else if( (thisCheck == BLACK_CHECK_KING && (otherSpace == RED_CHECK || otherSpace == RED_CHECK_KING)) ||
				  (thisCheck == RED_CHECK_KING && (otherSpace == BLACK_CHECK || otherSpace == BLACK_CHECK_KING))){ // Kings don't need to Move in any specific Direction
			return true;
		}else{ // No situation above is applicable; Either not Moving in the correct Direction or not Jumping over an Enemy
			return false;
		}
	}
	
	/**
	 * Checks if a Player Check that just Jumped is able to Jump again
	 * 
	 * @param check Point on the Board of the Player Check
	 * @return True if the Check is able to Jump again, else returns false
	 */
	private boolean checkDoubleJump(Point check){
		
		int checkX = (int) check.getX();
		int checkY = (int) check.getY();
		
		int thisCheck = boardState[checkX][checkY];
		
		for(int dX = -1; dX <= 1; dX++){ // Cycles through all possible next Moves
			for(int dY = -1; dY <= 1; dY++){
				if(dX != 0 && dY != 0){ // Won't run if either dX or dY == 0
					
					Point moveTo = new Point((int) check.getX() + dX, (int) check.getY() + dY);
					
					if(thisCheck == BLACK_CHECK && dY > 0){ // Black Checks must Move down the screen; Y increases
						if(ableToJump(check, moveTo)) return true;
					}else if(thisCheck == RED_CHECK && dY < 0){ // Red Checks must Move up the screen; Y decreases
						if(ableToJump(check, moveTo)) return true;
					}else{ // Kings don't need to Move in any specific Direction
						if(ableToJump(check, moveTo)) return true;
					}
										
				}
			}
		}
		
		return false; // Unable to make any additional Jumps
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
		
		assert isPointInBounds(moveThis);
		assert isPointInBounds(moveTo);
		
		// Checks the Movement of the Player's Check; if able to Move, update the Board
		if(!checkMovement(moveThis, moveTo)) return false; // If unable to Move, return false
		
		
		
		// Must be able to Move at this point; check if it's a Jump or not
		
		if(ableToJump(moveThis, moveTo)){ // If ableToJump, this Move must be a Jump
			int dX = (int) moveThis.getX()*2 - (int) moveTo.getX()*2;
			int dY = (int) moveThis.getY()*2 - (int) moveTo.getY()*2;
			
			boardState[dX][dY] = boardState[(int) moveThis.getX()][(int) moveThis.getY()];
			boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY;
			
			if(checkDoubleJump(new Point(dX, dY))){ // Checks if the Check that Jumped can Double Jump
				doubleJumpCheck = new Point(dX, dY); // Able to Double Jump; stores the Check that can Double Jump
			}else{
				doubleJumpCheck = null;
			}
			
		}else{ // Unable to Jump; must be a regular Move
			boardState[(int) moveTo.getX()][(int) moveTo.getY()] = boardState[(int) moveThis.getX()][(int) moveThis.getY()]; // Updates the Space being moved to
			doubleJumpCheck = null;
		}
		
		boardState[(int) moveThis.getX()][(int) moveThis.getY()] = EMPTY; // Updates the Check being moved; in either case, the Space becomes Empty
		
		if(doubleJumpCheck == null){ // Unable to Double Jump; the Turn is over, and the next Player can Move
			nextPlayer = (nextPlayer + 1) % 2;
		}
		return true;
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
		
		blackChecksLeft = STARTCHECKS;
		redChecksLeft = STARTCHECKS;
		
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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BoardGameState> Set<T> possibleBoardGameStates(T currentState) {
		
		Set<T> possibleNonJump = new HashSet<T>();
		Set<T> possibleJump = new HashSet<T>();
		
		List<Point> checkMoves = new ArrayList<Point>();
		
		
		if(doubleJumpCheck != null){ // If able to Double Jump, can only return possible Moves for the one Check
			checkMoves.add(doubleJumpCheck);
		}else{ // Don't have to complete a Double Jump; continue as normal
		
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					if(nextPlayer == BLACK_CHECK){ // Black Check Player is going
						if(boardState[i][j] == BLACK_CHECK || boardState[i][j] == BLACK_CHECK_KING){
							checkMoves.add(new Point(i, j));
						}
					}else{ // Red Check Player is going
						if(boardState[i][j] == RED_CHECK || boardState[i][j] == RED_CHECK_KING){
							checkMoves.add(new Point(i, j));
						}
					}
				}
			}
		
		}
		
		for(Point check : checkMoves){ // Cycles through all Chips
			
			int checkX = (int) check.getX();
			int checkY = (int) check.getY();
			
			for(int dX = -1; dX <= 1; dX++){
				for(int dY = -1; dY <= 1; dY++){
					if(dX != 0 && dY != 0){ // Does not run if either dX or dY are 0
						int x = checkX + dX;
						int y = checkY + dY;
						
						Point moveTo = new Point(x, y);
						if(isPointInBounds(moveTo)){ // Only runs if both x and y are on the Board
							
							CheckersState temp = (CheckersState) currentState.copy();

							if(checkMovement(check, moveTo)){ // If able to Move, check if it's a Jump or not
								if(ableToJump(check, moveTo)){ // Able to Jump
									temp.move(check, moveTo);
									possibleJump.add((T) temp);
								}else{ // Unable to Jump
									temp.move(check, moveTo);
									possibleNonJump.add((T) temp);
								}
							}
							
						}
					}
				}
			}
		}
				
		if(!possibleJump.isEmpty()){ // If Jumps are possible, forced to Jump
			return possibleJump;
		}else{
			return possibleNonJump;
		}
	}

	@Override
	public void setupStartingBoard() {
		boardState = newCheckBoard();
		doubleJumpCheck = null;
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
		// Need to account for having King pieces
		//return new char[]{'b', 'B', 'r', 'R', 'X'};
		return new char[]{'B', 'R'};
	}

	@Override
	public Color[] getPlayerColors() {
		// Problem here: each player index actually has two associated colors corresponding to different piece types
		return new Color[]{Color.black, Color.black, Color.red, Color.red};
	}
}
