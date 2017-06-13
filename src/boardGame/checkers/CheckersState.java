package boardGame.checkers;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class CheckersState extends TwoDimensionalBoardGameState {

	public static final int BOARD_WIDTH = 8;	
	public static final int STARTCHECKS = 12;
	
	public static final int BLACK_CHECK = 0; // Player 1 controls Black Checks, at the top of the Board
	public static final int BLACK_CHECK_KING = 2;

	public static final int RED_CHECK = 1; // Player 2 controls Red Checks, at the bottom of the Board
	public static final int RED_CHECK_KING = 3;
	
	private final static int NUMBER_OF_PLAYERS = 2;
	
	int movesSinceLastJump;
	int movesSinceNon_King;
	
	public Point doubleJumpCheck; // Used to keep track of a Check that can Double Jump
	
	/**
	 * Default Constructor
	 */
	public CheckersState(){
		super(NUMBER_OF_PLAYERS);
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
	CheckersState(int[][] newBoard, int player, List<Integer> win){
		super(newBoard, 2, player, win);
	}
	
	public int getNumPlayers(){
		return NUMBER_OF_PLAYERS;
	}
	
	/**
	 * Returns the Index of the winner of the Game
	 * 
	 * @return -1 if not at an endState, 0 if there's a Tie, 1 if Player 1 wins, or 2 if Player 2 wins
	 */
	public List<Integer> getWinners(){
		
		if(endState() && winners.size() == 0){ // If one Player is unable to make a Move, winners is updated by endState()
			
			int blackChecksLeft = 0;
			int redChecksLeft = 0;
			
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					int space = boardState[i][j];
					if(space ==  BLACK_CHECK || space == BLACK_CHECK_KING){
						blackChecksLeft++;
					}else if(space ==  RED_CHECK || space == RED_CHECK_KING){
						redChecksLeft++;
					}
				}
			}
			
			if(blackChecksLeft > redChecksLeft){
				winners.add(BLACK_CHECK);
			}else if(redChecksLeft > blackChecksLeft){
				winners.add(RED_CHECK);
			}else{ // Likely improbable; the new Draw state makes this a possibility, though
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
		
		int blackChecksLeft = 0;
		int redChecksLeft = 0;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				int space = boardState[i][j];
				if(space ==  BLACK_CHECK || space == BLACK_CHECK_KING){
					blackChecksLeft++;
				}else if(space ==  RED_CHECK || space == RED_CHECK_KING){
					redChecksLeft++;
				}
			}
		}

		// Returns True if there are no more of one Player's Checks left or if there are no more possible Moves
		if(blackChecksLeft == 0 || redChecksLeft == 0){
			return true;
		}else if(possibleBoardGameStates(this).size() == 0){
			winners.add((nextPlayer + 1) % 2); // One Player can't make a Move; other Player is the winner
			return true;
		}else if(movesSinceLastJump > 50 && movesSinceNon_King > 50){ // A Draw has been reached
			return true;
		}
		return false;
	}

	/**
	 * Checks if a Move is valid
	 * 
	 * @param moveThis Point on the Board of the Check being Moved
	 * @param moveTo Point on the Board being Moved to
	 * @return True if the Move is valid, else returns false
	 */
	private boolean checkMovement(Point moveThis, Point moveTo){

		int dX = (int) (moveTo.getX() - moveThis.getX()); // If moveThis > moveTo, dX < 0; Check Moves Up
		int dY = (int) (moveTo.getY() - moveThis.getY()); // If moveThis > moveTo, dX < 0; Check Moves Left
		
		int thisCheck = boardState[(int) moveThis.getX()][(int) moveThis.getY()];
		
		// Cannot Move an Empty Space or an Enemy Check
		if(thisCheck == EMPTY){
			return false;
		}else if(nextPlayer == BLACK_CHECK && (thisCheck == RED_CHECK || thisCheck == RED_CHECK_KING)){
			return false;
		}else if(nextPlayer == RED_CHECK && (thisCheck == BLACK_CHECK || thisCheck == BLACK_CHECK_KING)){
			return false;
		}
		
		if(thisCheck == BLACK_CHECK && dX < 0){ // Black Checks move down the Board; X increases, dX > 0
			return false;
		}else if(thisCheck == RED_CHECK && dX > 0){ // Red Checks move down the Board; X decreases, dX < 0
			return false;
		} // King's Directional Movement doesn't need to be checked
		
		if(!((int) Math.abs(dX) == 1 && (int) Math.abs(dY) == 1)){
			return false; // Both the X-Offset and the Y-Offset must be exactly 1; Jumping selects the Enemy Check being Jumped
		}
		
		if( checkForcedJump().size() > 0 && !checkForcedJump().contains(moveThis)) return false; // Did not take a Forced Jump; return false
		
		if(doubleJumpCheck != null){ // If able, must complete a Double Jump
			if((int) moveThis.getX() != (int) doubleJumpCheck.getX() || (int) moveThis.getY() != (int) doubleJumpCheck.getY()) return false;
		}
		
		if(boardState[(int) moveTo.getX()][(int) moveTo.getY()] == EMPTY){
			return true;
		}else{
			return ableToJump(moveThis, moveTo);			
		}
	}
	
	/**
	 * Checks if the Check is able to Jump over the specified Point
	 * 
	 * @param moveThis Point on the Board of the Check being Moved
	 * @param moveTo Point on the Board being Jumped over
	 * @return True if the Jump is valid, else returns false
	 */
	private boolean ableToJump(Point moveThis, Point moveTo){
		
		// Double Jump Check made in checkMovement; don't need to double-check here
		
		int jumpX = (int) moveTo.getX() + (int) (moveTo.getX() - moveThis.getX());
		int jumpY = (int) moveTo.getY() + (int) (moveTo.getY() - moveThis.getY());
				
		if(!isPointInBounds(new Point(jumpX, jumpY))) return false;
		if(boardState[jumpX][jumpY] != EMPTY) return false;
		
		int thisCheck = boardState[(int) moveThis.getX()][(int) moveThis.getY()];
		int otherCheck = boardState[(int) moveTo.getX()][(int) moveTo.getY()];
		
		if((thisCheck == BLACK_CHECK || thisCheck == BLACK_CHECK_KING) && (otherCheck == RED_CHECK || otherCheck == RED_CHECK_KING)){
			return true;
		}else if((thisCheck == RED_CHECK || thisCheck == RED_CHECK_KING) && (otherCheck == BLACK_CHECK || otherCheck == BLACK_CHECK_KING)){
			return true;
		}else{
			return false;
		}
		
		// Directional Movement Checks are handled by checkMovement; everything should be fine by this point
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
					
					Point moveTo = new Point(checkX + dX, checkY + dY);
					if(isPointInBounds(moveTo)){
					
						if((thisCheck == BLACK_CHECK_KING || thisCheck == RED_CHECK_KING) && ableToJump(check, moveTo)){
							doubleJumpCheck = check;
							return true;							
						}else if(thisCheck == BLACK_CHECK && dX > 0 && ableToJump(check, moveTo)){ // Black Checks move down the Board; X increases, dX > 0
							doubleJumpCheck = check;
							return true;
						}else if(thisCheck == RED_CHECK && dX < 0 && ableToJump(check, moveTo)){ // Red Checks move down the Board; X decreases, dX < 0
							doubleJumpCheck = check;
							return true;
						}	
					}

				}			
			}
		}
		
		
		doubleJumpCheck = null;
		return false; // Unable to make any additional Jumps
	}
	
	private List<Point> checkForcedJump(){
		
		List<Point> checkMoves = new ArrayList<Point>();
		List<Point> ableToJump = new ArrayList<Point>();
		
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
		
		for(Point check : checkMoves){
			if(checkDoubleJump(check)) ableToJump.add(check);
		}
		
		return ableToJump;
	}
	
	/**
	 * Moves a Check from one position in the CheckBoard to another diagonally
	 * 
	 * @param players Index of the Player whose turn it is
	 * @param moveThis Point of the Check to be moved
	 * @param moveTo Point where the selected Check will be moved to
	 * @return True if able to move the selected Check to the new space, else returns false
	 */
	@Override
	public boolean moveDoublePoint(Point moveThis, Point moveTo){
		
		assert isPointInBounds(moveThis);
		assert isPointInBounds(moveTo);
		
		// Checks the Movement of the Player's Check; if able to Move, update the Board
		if(!checkMovement(moveThis, moveTo)) return false; // If unable to Move, return false
		
		
		
		// Must be able to Move at this point; check if it's a Jump or not
		
		int thisCheck = boardState[(int) moveThis.getX()][(int) moveThis.getY()];
		int finalX = -1; // Used to store the X where the Check lands
		int finalY = -1; // Used to store the Y where the Check lands
		
		if(boardState[(int) moveTo.getX()][(int) moveTo.getY()] == EMPTY){ // Must be a regular Move
			boardState[(int) moveTo.getX()][(int) moveTo.getY()] = thisCheck;
			finalX = (int) moveTo.getX();
			finalY = (int) moveTo.getY();
			movesSinceLastJump++;
		}else{ // Must be a Jump
			int jumpX = (int) moveTo.getX() + (int) (moveTo.getX() - moveThis.getX());
			int jumpY = (int) moveTo.getY() + (int) (moveTo.getY() - moveThis.getY());
			
			boardState[jumpX][jumpY] = thisCheck;
			boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY;
			finalX = jumpX;
			finalY = jumpY;
			
			movesSinceLastJump = 0;
			checkDoubleJump(new Point(jumpX, jumpY));
		}
		
		if(thisCheck == BLACK_CHECK_KING || thisCheck == RED_CHECK_KING){
			movesSinceNon_King++;
		}else{
			movesSinceNon_King = 0;
		}
		
		if(doubleJumpCheck == null){
			nextPlayer = (nextPlayer + 1) % 2;
		}
		
		if(thisCheck == BLACK_CHECK && finalX == BOARD_WIDTH-1){ // Black Check reached the other side
			boardState[finalX][finalY] = BLACK_CHECK_KING;
		}else if(thisCheck == RED_CHECK && finalX == 0){ // Red Check reached the other side
			boardState[finalX][finalY] = RED_CHECK_KING;
		}
		
		boardState[(int) moveThis.getX()][(int) moveThis.getY()] = EMPTY;
		return true;
	}
	
	/**
	 * Makes and returns a BoardGameState that is a duplicate of this BoardGameState
	 * 
	 * @return BoardGameState that is a duplicate of this BoardGameState
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CheckersState copy() {
		CheckersState temp = new CheckersState(this);
		temp.movesSinceLastJump = this.movesSinceLastJump;
		temp.movesSinceNon_King = this.movesSinceNon_King;
		return temp;
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
									temp.moveDoublePoint(check, moveTo);
									possibleJump.add((T) temp);
									possibleNonJump.clear();
								}else{ // Unable to Jump
									temp.moveDoublePoint(check, moveTo);
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
		movesSinceLastJump = 0;
		movesSinceNon_King = 0;
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
		return new char[]{'b', 'r', 'B', 'R'};
	}

	@Override
	public Color[] getPlayerColors() {
		// Problem here: each player index actually has two associated colors corresponding to different piece types
		return new Color[]{Color.black, Color.red, new Color(64,64,64), new Color(255,81,81)};
	}

	/**
	 * Unused by this BoardGame; always returns false
	 */
	@Override
	public boolean moveSinglePoint(Point goTo) {
		return false;
	}

	/**
	 * Checkers requires two Points to move; returns false
	 */
	@Override
	public boolean moveOnePiece() {
		return false;
	}
}
