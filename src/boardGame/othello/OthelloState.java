package boardGame.othello;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class OthelloState extends TwoDimensionalBoardGameState {
	
	int numPasses;
	
	private final int BOARD_WIDTH = 8;

	private final static int NUMBER_OF_PLAYERS = 2;
	
	private final int BOARD_CORE1 = 3; // Keeps track of the center of the Board
	private final int BOARD_CORE2 = 4; // Keeps track of the center of the Board
	
	public static final int EMPTY = -1;
	public static final int BLACK_CHIP = 0;
	public static final int WHITE_CHIP = 1;
	
	/**
	 * Default Constructor
	 */
	public OthelloState(){
		super(NUMBER_OF_PLAYERS);
	}
	
	/**
	 * Copy Constructor; Allows the User to specify which pieces are where on the Board
	 * 
	 * @param newBoard int[][] representing the new Board
	 * @param nextPlayer Player whose turn it is
	 * @param newWinners List<Integer> containing the indexes of the current Winners
	 */
	public OthelloState(OthelloState state){
		super(state);
	}

	public OthelloState(int[][] board, int nextPlay, List<Integer> win){
		super(board, NUMBER_OF_PLAYERS, nextPlay, win);
	}
	
	public int getNumPlayers(){
		return NUMBER_OF_PLAYERS;
	}
	
	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the Board is completely filled, else returns false
	 */
	@Override
	public boolean endState() {
		if(numPasses >= 2){
			return true;
		}
		return false;
	}

	/**
	 * Checks the Winners of this current BoardGameState and updates the winners List
	 */
	private void checkWinners(){
		if(endState() && winners.isEmpty()){ // Won't run unless an EndState is reached
			
			int blackChipCount = 0;
			int whiteChipCount = 0;
			
			for(int i = 0; i < BOARD_WIDTH; i++){
				for(int j = 0; j < BOARD_WIDTH; j++){
					if(boardState[i][j] == BLACK_CHIP){
						blackChipCount++;
					}else if(boardState[i][j] == WHITE_CHIP){
						whiteChipCount++;
					}
				}
			}
			
			if(blackChipCount > whiteChipCount){
				winners.add(BLACK_CHIP);
			}else if(whiteChipCount > blackChipCount){
				winners.add(WHITE_CHIP);
			}else{
				winners.add(BLACK_CHIP);
				winners.add(WHITE_CHIP);
			}
		}
	}
	
	/**
	 * Allows a Player to make a Move on the current BoardGameState; Returns true if the Move was valid,
	 * and updates the List of winners
	 * 
	 * @param goTo Point representing an Empty Space being played towards
	 * @return True if the Move was successful, else returns false
	 */
	@Override
	public boolean moveSinglePoint(Point goTo){
		
		int goX = (int) goTo.getX();
		int goY = (int) goTo.getY();

		assert goX >= 0 && goX < BOARD_WIDTH;
		assert goY >= 0 && goY < BOARD_WIDTH;
						
		boolean check1 = boardState[goX][goY] == EMPTY;
		if(!check1) return false; // Cannot move to a Non-Empty Space; y problem not due to Non-Empty Spaces
		
		boolean ableToMove = false;
		
		for(int dX = -1; dX <=1; dX++){ // Works off of the same idea as possibleBoardStates()
			for(int dY = -1; dY <=1; dY++){
				if(dX != 0 || dY != 0){ // Does not run if both dX and dY are 0
					
					int x = goX + dX; // Stores the offset Space
					int y = goY + dY;
					
					if(!isPointInBounds(new Point(x, y))) continue; // Out of Bounds; cannot use this Offset
					
					boolean foundEnemy = boardState[x][y] == (nextPlayer + 1) % 2; // Found an Enemy Chip
					
					if(foundEnemy){ // Only runs if an Enemy is present
						do{
							x += dX;  // Searches the next Space over
							y += dY;

						}while((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH) && (boardState[x][y] == (nextPlayer + 1) % 2)); // Continues while within bounds and Space has an Enemy Chip
						
						if((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH)){
							if(boardState[x][y] == nextPlayer){ // Found Player Chip at end of Line; able to make the Move
								ableToMove = true; // Was able to make at least 1 Move
								
								x = goX; // Stores the offset Space
								y = goY;
								
								do{
									boardState[x][y] = nextPlayer; // Updates the Space
									x += dX;  // Searches the next Space over
									y += dY;
								}while((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH) && (boardState[x][y] == (nextPlayer + 1) % 2)); // Only converts Enemy Chips
								
							} // End Able-to-Play If Statement
						}
					} // End foundEnemy Check
				} // End dX, dY Check
			}
		} // End Offset For-Loop
	
		if(ableToMove){ // Was able to Move; Update the nextPlayer and boardState, and return True
			boardState[goX][goY] = nextPlayer;
			nextPlayer = (nextPlayer + 1) % 2;
			checkWinners();
			numPasses = 0; // Just made a Move; reset passes
			return true;			
		}else{ // Unable to Move; return False
			checkWinners();
			return false;
		}
	}
	
	/**
	 * Creates a List of all possible valid Moves from this BoardGameState
	 * 
	 * @param currentState The BoardGameState being played from
	 * @return List<T> of all BoardGameStates possible from the given BoardGameState
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BoardGameState> Set<T> possibleBoardGameStates(T currentState) {
		
		Set<T> possible = new HashSet<T>();
		List<Point> chipMoves = new ArrayList<Point>();
		
		for(int i = 0; i < BOARD_WIDTH; i++){ // This part works
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == nextPlayer){
					chipMoves.add(new Point(i, j));
				}
			}
		}
		
		for(Point chip : chipMoves){ // Cycles through all Chips
			int chipX = (int) chip.getX();
			int chipY = (int) chip.getY();
			
			for(int dX = -1; dX <= 1; dX++){ // Doesn't handle Diagonals well; probably part of the Move check				
				for(int dY = -1; dY <= 1; dY++){
					if(dX != 0 || dY != 0){ // Cycles through the dX and dY correctly
						
						int x = chipX;
						int y = chipY;
						
						do{
							x += dX;
							y += dY;
						}while((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH) && boardState[x][y] == (nextPlayer + 1) % 2);
						
						if((x >= 0 && x < BOARD_WIDTH) && (y >= 0 && y < BOARD_WIDTH)){
							if(boardState[x][y] == EMPTY){
								OthelloState temp = (OthelloState) currentState.copy();
								if(temp.moveSinglePoint(new Point(x, y))){
									possible.add((T) temp);
								}
							}
						}
					}
				}
			}
		}
				
		// Schrum: this case here annoys me. I feel that there is a way to avoid it
		
		if(possible.isEmpty()){ // If unable to make a Move, must return the currentState; counts as a Pass
			possible.add((T) this.pass());
		}
		return possible;
	}

	/**
	 * Creates a copy of this BoardGameState
	 * 
	 * @return Copy of this BoardGameState
	 */
	@SuppressWarnings("unchecked")
	@Override
 	public OthelloState copy() {
		return new OthelloState(this);
	}

	/**
	 * Returns a List of all Winners from this BoardGameState
	 * 
	 * @return List<Integer> containing the Indexes from this BoardGameState
	 */
	@Override
	public List<Integer> getWinners() {
		checkWinners();
		return winners;
	}

	/**
	 * Sets up the Othello Board; only places the starting Chips in the center at the correct positions
	 */
	@Override
	public void setupStartingBoard() {
		numPasses = 0;

		boardState[BOARD_CORE1][BOARD_CORE1] = BLACK_CHIP;
		boardState[BOARD_CORE2][BOARD_CORE2] = BLACK_CHIP;
		
		boardState[BOARD_CORE1][BOARD_CORE2] = WHITE_CHIP;
		boardState[BOARD_CORE2][BOARD_CORE1] = WHITE_CHIP;

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
		return new char[]{'B', 'W'};
	}

	@Override
	public Color[] getPlayerColors() {
		return new Color[]{Color.black, Color.white};
	}
	
	OthelloState pass(){
		OthelloState temp = this.copy();
		temp.numPasses = this.numPasses + 1; // Can't use ++ here; not sure why
		temp.nextPlayer = (this.nextPlayer + 1) % 2;
		return temp;
	}
	
	/**
	 * Unused by this BoardGame; always returns false
	 */
	@Override
	public boolean moveDoublePoint(Point goTo, Point moveTo) {
		return false;
	}

	/**
	 * Othello only requires one Point to move; always returns true
	 */
	@Override
	public boolean moveOnePiece() {
		return true;
	}
	
}
