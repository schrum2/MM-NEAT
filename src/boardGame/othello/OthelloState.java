package boardGame.othello;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;

public class OthelloState implements BoardGameState{
	
	private int[][] boardState;
	private int currentPlayer;
	private List<Integer> winners;
	
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

		currentPlayer = 0;
		winners = new ArrayList<Integer>();
	}
	
	/**
	 * Copy Constructor; Allows the User to specify which pieces are where on the Board
	 * 
	 * @param newBoard int[][] representing the new Board
	 * @param nextPlayer Player whose turn it is
	 * @param newWinners List<Integer> containing the indexes of the current Winners
	 */
	public OthelloState(int[][] newBoard, int nextPlayer, List<Integer> newWinners){
		assert newBoard.length == BOARD_WIDTH && newBoard[0].length == BOARD_WIDTH;
		boardState = new int[BOARD_WIDTH][BOARD_WIDTH];
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				boardState[i][j] = newBoard[i][j];
			}
		}
		
		currentPlayer = nextPlayer;
		
		winners = new ArrayList<Integer>();
		winners.addAll(newWinners);
	}
	
	/**
	 * Returns an Array containing a description of the current BoardGameState
	 * 
	 * @return double[] representing the current BoardGameState
	 */
	@Override
	public double[] getDescriptor() {
		double[] boardArray = new double[BOARD_WIDTH*BOARD_WIDTH];
		int index = 0;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				boardArray[index++] = boardState[i][j];
			}
		}
		
		return boardArray;
	}

	/**
	 * Returns true if the Game is over, else returns false
	 * 
	 * @return True if the Board is completely filled, else returns false
	 */
	@Override
	public boolean endState() {
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
				System.out.println("Black Chip Wins!");
			}else if(whiteChipCount > blackChipCount){
				winners.add(WHITE_CHIP);
				System.out.println("White Chip Wins!");
			}else{
				winners.add(BLACK_CHIP);
				winners.add(WHITE_CHIP);
				System.out.println("It's a Tie!");
			}
		}
	}
	
	private boolean checkChipSelect(int useX, int useY){
		return boardState[useX][useY] == currentPlayer;
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
		
		// TODO: REWORK THIS! LIKE, A LOT! IT DOESN'T WORK!

		int useX = (int) useThis.getX();
		int useY = (int) useThis.getY();

		int goX = (int) goTo.getX();
		int goY = (int) goTo.getY();
		
		System.out.println("Check Select");

		boolean check1 = checkChipSelect(useX, useY);
		if(!check1) return false; // Cannot move if it is not your turn
		
		// Should always be true
		if(!((useX - goX == 0) || (useY - goY == 0) || (useX - goX == useY - goY))){ // Checks directional selection of the Move; can only be Vertical, Horizontal, or True Diagonal
			System.out.println("Can only select Vertically, Horizontally, or Diagonally. Selected: (" + useX + ", " + useY + ") Went to: (" + goX + ", " + goY + ")");
			return false;
		}else if(boardState[goX][goY] != EMPTY){ // Can only select an Empty Space to Move to
			System.out.println("Can only select an Empty Space for the end of the Move: (" + goX + ", " + goY + ")");
			return false;
		}
		
		boolean vert = useX - goX == 0;
		boolean hori = useY - goY == 0;
		boolean tl_br_diag = useX - goX == useY - goY; // Top-Left to Bottom-Right Diagonal
		boolean bl_tr_diag = useX - goX == -(useY - goY); // Bottom-Left to Top-Right Diagonal

		System.out.println("Check Move");

		// Checks that the entire Move is valid; Only enemy pieces in-between the selected Chip and selected Empty Space
		
		if(vert){
			for(int i = Math.min(useY, goY); i < Math.max(useY, goY); i++){
				if((boardState[useX][i] != (currentPlayer + 1 % 2) || boardState[useX][i] != EMPTY) && i != useY){
					System.out.println("Cannot move to selected Space: (" + goX + ", " + goY + "); there is a(n) " + label(boardState[useX][i]) + " at (" + useX + ", " + useY + ")");
					return false;
				}
			}
		}else if(hori){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				if((boardState[i][useY] != (currentPlayer + 1 % 2) && boardState[i][useY] != EMPTY) && i != useY){
					System.out.println("Cannot move to selected Space: (" + goX + ", " + goY + "); there is a(n) " + label(boardState[useX][i]) + " at (" + useX + ", " + useY + ")");
					return false;
				}
			}
		}else if(tl_br_diag){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				for(int j = Math.min(useY, goY); j < Math.max(useY, goY); j++){
					if((boardState[i][j] != (currentPlayer + 1 % 2) && boardState[i][j] != EMPTY)&& i != useY){
						System.out.println("Cannot move to selected Space: (" + goX + ", " + goY + "); there is a(n) " + label(boardState[useX][i]) + " at (" + useX + ", " + useY + ")");
						return false;
					}
				}
			}
		}else if(bl_tr_diag){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				for(int j = Math.max(useY, goY); j < Math.min(useY, goY); j--){
					if((boardState[i][j] != (currentPlayer + 1 % 2) && boardState[i][j] != EMPTY) && i != useY){
						System.out.println("Cannot move to selected Space: (" + goX + ", " + goY + "); there is a(n) " + label(boardState[useX][i]) + " at (" + useX + ", " + useY + ")");
						return false;
					}
				}
			}
		}

		//System.out.println("Did it!");
		
		// Survived the Trials of Logic; Valid Move
		
		// Converts all enemy Chips along the Move to the Player's Chips
		if(vert){
			for(int i = Math.min(useY, goY); i < Math.max(useY, goY); i++){
				boardState[useX][i] = currentPlayer;
				System.out.println("Vert: (" + useX + ", "+ useY + ") to (" + goX + ", " + goY + ").");
			}
		}else if(hori){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				boardState[i][useY] = currentPlayer;
				System.out.println("Hori: (" + useX + ", "+ useY + ") to (" + goX + ", " + goY + ").");
			}
		}else if(tl_br_diag){ // Ensure that this works...
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				for(int j = Math.min(useY, goY); j < Math.max(useY, goY); j++){
					boardState[i][j] = currentPlayer;
					System.out.println("tl_br_diag: (" + useX + ", "+ useY + ") to (" + goX + ", " + goY + ").");
				}
			}
		}else if(bl_tr_diag){ // Ensure that this works...
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				for(int j = Math.max(useY, goY); j < Math.min(useY, goY); j--){
					boardState[i][j] = currentPlayer;
					System.out.println("bl_tr_diag: (" + useX + ", "+ useY + ") to (" + goX + ", " + goY + ").");
				}
			}
		}
		boardState[goX][goY] = currentPlayer; // Places the Player's Chip at the end of the Move
		checkWinners();
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
		System.out.println(currentPlayer);
		return true;
	}
	
	/**
	 * Returns a String representing the specified Index
	 * 
	 * @param index Integer being represented as a String
	 * @return Label of the given Index
	 */
	private String label(int index) {
		switch(index){
			case EMPTY: return "Empty Space";
			case BLACK_CHIP: return "Black Chip";
			case WHITE_CHIP: return "White Chip";
		}
		return "Invalid Space Marker";
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
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == currentPlayer){
					chipMoves.add(new Point(i, j));
				}
			}
		}
				
		for(Point chip : chipMoves){
			int chipX = (int) chip.getX();
			int chipY = (int) chip.getY();
			
			for(int dX = -1; dX <= 1; dX++){
				for(int dY = -1; dY <= 1; dY++){
					if(dX != 0 || dY != 0){
						int x = chipX;
						int y = chipY;
						do{
							x = chipX + dX;
							y = chipY + dY;
						}while(boardState[x][y] == (currentPlayer + 1) % 2);
						if((x > 0 && x < BOARD_WIDTH) && (y > 0 && y < BOARD_WIDTH)){
							if(boardState[x][y] == EMPTY){
								OthelloState temp = (OthelloState) currentState.copy();
								temp.move(chip, new Point(x, y));
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
	


	/**
	 * Creates a copy of this BoardGameState
	 * 
	 * @return Copy of this BoardGameState
	 */
	@Override
 	public BoardGameState copy() {
		int[][] copy = new int[BOARD_WIDTH][BOARD_WIDTH];
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				copy[i][j] = boardState[i][j];
			}
		}
		
		ArrayList<Integer> newWin = new ArrayList<Integer>();
		newWin.addAll(winners);
		
		OthelloState temp = new OthelloState(copy, currentPlayer, newWin);
		return temp;
	}

	/**
	 * Returns a List of all Winners from this BoardGameState
	 * 
	 * @return List<Integer> containing the Indexes from this BoardGameState
	 */
	@Override
	public List<Integer> getWinner() {
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
	
}
