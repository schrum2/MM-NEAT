package boardGame.checkers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		
		int checkX = (int) moveThis.getX();
		int checkY = (int) moveThis.getY();
		
		int otherX = (int) moveTo.getX();
		int otherY = (int) moveTo.getY();
		
		int thisCheck = boardState[checkX][checkY];
		int otherSpace = boardState[otherX][otherY];
		
		// Prevents the Player from moving a piece that isn't their color of Check
		if(nextPlayer == BLACK_CHECK){
			if(thisCheck != BLACK_CHECK && thisCheck != BLACK_CHECK_KING){ // Player 1 attempted to move something that wasn't a Black Check
				return false;
			}
		}else{
			if(thisCheck != RED_CHECK && thisCheck != RED_CHECK_KING){ // Player 2 attempted to move something that wasn't a Red Check
				return false;
			}
		}
		
		int dX = checkX - otherX;
		int dY = checkY - otherY;
		
		if(Math.abs(dX) != 1 || Math.abs(dY) != 1){
			return false;
		}else if(thisCheck == BLACK_CHECK && dX < 0){ // Black Checks only Move Down; X increases, dX must be > 0
			return false;
		}else if(thisCheck == RED_CHECK && dX > 0){ // Red Checks can only move Up; X decreases, dX must be < 0
			return false;
		} // Kings can Move both Up and Down; no check needed
		
		// TODO: Handle Movement Mechanics; still not working right now.
		
		if(otherSpace == EMPTY){ // Move to Empty Space; Auto-Succeed Jamboree
			movePlayerPiece(moveThis, moveTo, false);
			
			// Handles being "Kinged"
			if(thisCheck == BLACK_CHECK && otherX == 7){
				boardState[otherX][otherY] = BLACK_CHECK_KING;
			}else if(thisCheck == RED_CHECK && otherX == 0){
				boardState[otherX][otherY] = RED_CHECK_KING;
			}
			
			nextPlayer = (nextPlayer + 1) % 2;
			return true;
		}else if(thisCheck == BLACK_CHECK && otherSpace == RED_CHECK){
			otherX = otherX + dX; // Doubles the Move made; attempt to Jump
			otherY = otherY + dY; // Doubles the Move made; attempt to Jump
			if(((otherX >= 0 && otherX < BOARD_WIDTH) && (otherY >= 0 && otherY < BOARD_WIDTH)) && boardState[otherX][otherY] == EMPTY){ // otherX and otherY are on the Board; able to Jump
				movePlayerPiece(moveThis, new Point(otherX, otherY), false);
				boardState[(int) moveTo.getX()][(int) moveTo.getY()] = EMPTY;
				return true;
			}else{ // Not able to Jump; not a valid Move
				return false;
			}
		}else{ // Not attempting to Jump; not a valid Move
			return false;
		}
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
	public <T extends BoardGameState> List<T> possibleBoardGameStates(T currentState) {
		// TODO: Ensure that this method works.
		
		List<CheckersState> possible = new ArrayList<CheckersState>();
		List<Point> checkMoves = new ArrayList<Point>();
		
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
		
		for(Point check : checkMoves){ // Cycles through all Chips
			
			int checkX = (int) check.getX();
			int checkY = (int) check.getY();
			int checkType = boardState[checkX][checkY];

			for(int dX = -1; dX <= 1; dX++){
				for(int dY = -1; dY <= 1; dY++){
					if(dX != 0 && dY != 0){ // Does not run if either dX or dY are 0
						int x = checkX + dX;
						int y = checkY + dY;
						
						if((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH)){ // Only runs if both x and y are on the Board
							
							CheckersState temp = (CheckersState) currentState.copy();
							boolean moved = false;
							
							if(checkType == BLACK_CHECK && dX > 0){ // Black Checks only Move Down; X increases, dX must be > 0
								moved = temp.move(check, new Point(x, y));
							}else if(checkType == RED_CHECK && dX < 0){ // Red Checks only Move Up; X decreases, dX must be < 0
								moved = temp.move(check, new Point(x, y));
							}else if(checkType == BLACK_CHECK_KING || checkType == RED_CHECK_KING){ // Kings move any Direction so long as it's on the Board
								moved = temp.move(check, new Point(x, y));
							}
							
							if(moved){ // If the Move was successful in the above check, add it to the List of possible Moves
								possible.add(temp);
							}
							
						}
					}
				}
			}
		}
		
		List<T> returnThis = new ArrayList<T>();
		returnThis.addAll((Collection<? extends T>) possible);
		
		return returnThis;
	}

	@Override
	public void setupStartingBoard() {
		boardState = newCheckBoard();
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
		return new char[]{'b', 'B', 'r', 'R', 'X'};
	}
}
