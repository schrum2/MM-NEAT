package boardGame.othello;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class OthelloState extends TwoDimensionalBoardGameState {
	
	private int numPasses;
	
	private final int BOARD_WIDTH = 8;

	private final int BOARD_CORE1 = 3; // Keeps track of the center of the Board
	private final int BOARD_CORE2 = 4; // Keeps track of the center of the Board
	
	private final int EMPTY = -1;
	private final int BLACK_CHIP = 0;
	private final int WHITE_CHIP = 1;
	
	/**
	 * Default Constructor
	 */
	public OthelloState(){
		super(2);
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

	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the Board is completely filled, else returns false
	 */
	@Override
	public boolean endState() {

		if(numPasses > 1) return true;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == EMPTY){
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Checks the Winners of this current BoardGameState and updates the winners List
	 */
	private void checkWinners(){
		if(endState()){
			
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
	 * @param useThis Point representing the current Chip being used to make a Move
	 * @param goTo Point representing an Empty Space being played towards
	 * @return True if the Move was successful, else returns false
	 */
	public boolean move(Point useThis, Point goTo){
		
		int useX = (int) useThis.getX();
		int useY = (int) useThis.getY();

		int goX = (int) goTo.getX();
		int goY = (int) goTo.getY();

		assert useX >= 0 && useX < BOARD_WIDTH;
		assert useY >= 0 && useY < BOARD_WIDTH;
		assert goX >= 0 && goX < BOARD_WIDTH;
		assert goY >= 0 && goY < BOARD_WIDTH;
				
		boolean check1 = boardState[useX][useY] == nextPlayer;
		if(!check1) return false; // Cannot move if it is not your chip
		
		boolean check2 = boardState[goX][goY] == EMPTY;
		if(!check2) return false; // Cannot move to a Non-Empty Space
		
		boolean check3 = /*Vertical*/ (useX - goX == 0) || /*Horizontal*/ (useY - goY == 0) || /*Slope of 1 Diagonal*/ (useX - goX == useY - goY) || /*Slope of -1 Diagonal*/ (useX - goX == -(useY - goY));
		if(!check3) return false; // Cannot move in a non-linear way
		
		boolean foundEnemy = false;
		boolean includesSelf = false;
		
		if(useX - goX == 0){ // Vertical
			for(int i = Math.min(useY, goY)+1; i < Math.max(useY, goY); i++){ // Will never be at useY or goY
				if(boardState[useX][i] == (nextPlayer + 1) % 2) foundEnemy = true;
				if(boardState[useX][i] == nextPlayer) includesSelf = true;
			}
		}else if(useY - goY == 0){ // Horizontal
			for(int i = Math.min(useX, goX)+1; i < Math.max(useX, goX); i++){ // Will never be at useX or goX
				if(boardState[i][useY] == (nextPlayer + 1) % 2) foundEnemy = true;
				if(boardState[i][useY] == nextPlayer) includesSelf = true;
			}
		}else if(useX - goX == useY - goY){
			for(int i = 1; i < Math.abs(useX - goX); i++){ // Stores the displacement value; will never start at useThis
				if(useX - goX < 0){ // Going down-right; goX is greater
					if(boardState[useX + i][useY + i] == (nextPlayer + 1) % 2) foundEnemy = true;
					if(boardState[useX + i][useY + i] == nextPlayer) includesSelf = true;
				}else{ // Going up-left; goX is smaller
					if(boardState[useX - i][useY - i] == (nextPlayer + 1) % 2) foundEnemy = true;
					if(boardState[useX - i][useY - i] == nextPlayer) includesSelf = true;
				}
			}
		}else if(useX - goX == -(useY - goY)){
			for(int i = 1; i < Math.abs(useX - goX); i++){ // Stores the displacement value; will never start at useThis
				if(useX - goX < 0){ // Going up-right; goX is greater
					if(boardState[useX + i][useY - i] == (nextPlayer + 1) % 2) foundEnemy = true;
					if(boardState[useX + i][useY - i] == nextPlayer) includesSelf = true;
				}else{ // Going down-left; goX is smaller
					if(boardState[useX - i][useY + i] == (nextPlayer + 1) % 2) foundEnemy = true;
					if(boardState[useX - i][useY + i] == nextPlayer) includesSelf = true;
				}
			}
		}
		
		if(!foundEnemy || includesSelf) return false; // Can only have Enemy Chips in-between the Player Chip and the Empty Space
		
		if(useX - goX == 0){ // Vertical
			for(int i = Math.min(useY, goY); i < Math.max(useY, goY); i++){
				boardState[useX][i] = nextPlayer;
			}
		}else if(useY - goY == 0){ // Horizontal
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				boardState[i][useY] = nextPlayer;
			}
		}else if(useX - goX == useY - goY){
			for(int i = 1; i < Math.abs(useX - goX); i++){ // Stores the displacement value
				if(useX - goX < 0){ // Going down-right; goX is greater
					boardState[useX + i][useY + i] = nextPlayer;
				}else{ // Going up-left; goX is smaller
					boardState[useX - i][useY - i] = nextPlayer;
				}
			}
		}else if(useX - goX == -(useY - goY)){
			for(int i = 1; i < Math.abs(useX - goX); i++){ // Stores the displacement value
				if(useX - goX < 0){ // Going up-right; goX is greater
					boardState[useX + i][useY - i] = nextPlayer;
				}else{ // Going down-left; goX is smaller
					boardState[useX - i][useY + i] = nextPlayer;
				}
			}			
		}
		
		boardState[goX][goY] = nextPlayer;
		nextPlayer = (nextPlayer + 1) % 2;
		checkWinners();
		return true;
	}
	
	/**
	 * Creates a List of all possible valid Moves from this BoardGameState
	 * 
	 * @param currentState The BoardGameState being played from
	 * @return List<T> of all BoardGameStates possible from the given BoardGameState
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends BoardGameState> List<T> possibleBoardGameStates(T currentState) {
		
		List<OthelloState> possible = new ArrayList<OthelloState>();
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
								if(temp.move(chip, new Point(x, y))){
									possible.add(temp);
								}
							}
						}
					}
				}
			}
		}
		
		List<T> returnThis = new ArrayList<T>();
		returnThis.addAll((Collection<? extends T>) possible);
		
		if(returnThis.isEmpty()){ // If unable to make a Move, must return the currentState; counts as a Pass
			returnThis.add(currentState);
			numPasses++;
			return returnThis;
		}else{
			return returnThis;			
		}
	}

	/**
	 * Creates a copy of this BoardGameState
	 * 
	 * @return Copy of this BoardGameState
	 */
	@Override
 	public BoardGameState copy() {
		return new OthelloState(this);
	}

	/**
	 * Returns a List of all Winners from this BoardGameState
	 * 
	 * @return List<Integer> containing the Indexes from this BoardGameState
	 */
	@Override
	public List<Integer> getWinners() {
		return winners;
	}

	/**
	 * Creates and returns a String visually representing the current BoardGameState
	 * 
	 * @return String visually representing the current BoardGameState
	 */
	public String toString(){
		String result = "  0 1 2 3 4 5 6 7 \n  _ _ _ _ _ _ _ _ ";
		for(int i = 0; i < BOARD_WIDTH; i++){
			result += "\n" + i + "|";
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == EMPTY){
					result += " ";					
				}else if(boardState[i][j] == BLACK_CHIP){
					result += "B";									
				}else if(boardState[i][j] == WHITE_CHIP){
					result += "W";									
				}
				result += "|";
			}
			result += "\n  _ _ _ _ _ _ _ _";
		}
		
		return result;
	}

	@Override
	public void setupStartingBoard() {
		numPasses = 0;
		boardState = new int[BOARD_WIDTH][BOARD_WIDTH];
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				boardState[i][j] = EMPTY;
			}
		}
		
		boardState[BOARD_CORE1][BOARD_CORE1] = BLACK_CHIP;
		boardState[BOARD_CORE2][BOARD_CORE2] = BLACK_CHIP;
		
		boardState[BOARD_CORE1][BOARD_CORE2] = WHITE_CHIP;
		boardState[BOARD_CORE2][BOARD_CORE1] = WHITE_CHIP;

		nextPlayer = 0;
		winners = new ArrayList<Integer>();
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
	
}
