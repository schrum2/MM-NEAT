package boardGame.othello;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGameState;

public class OthelloState implements BoardGameState{
	
	private int[][] boardState;
	private int currentPlayer;
	private List<Integer> winners;
	
	private final int BOARD_WIDTH = 8;

	private final int BOARD_CORE1 = 4; // Keeps track of the center of the Board
	private final int BOARD_CORE2 = 5; // Keeps track of the center of the Board

	
	private final int EMPTY = -1;
	private final int BLACK_CHIP = 0;
	private final int WHITE_CHIP = 1;
	
	
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
	
	public OthelloState(int[][] newBoard, int nextPlayer, List<Integer> newWinners){
		assert newBoard.length == BOARD_WIDTH && newBoard[0].length == BOARD_WIDTH;
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				boardState[i][j] = newBoard[i][j];
			}
		}
		
		currentPlayer = nextPlayer;
		
		winners.addAll(newWinners);
	}
	
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

	public boolean move(Point useThis, Point goTo){

		int useX = (int) useThis.getX();
		int useY = (int) useThis.getY();

		int goX = (int) goTo.getX();
		int goY = (int) goTo.getY();

		if(boardState[useX][useY] == EMPTY){ // Cannot use an Empty Space to make a Move
			System.out.println("Cannot select an Empty Space to make a Move: (" + useX + ", " + useY + ")");
			return false;
		}else if(boardState[useX][useY] != currentPlayer){ // Cannot select the other Player's Chips to make a Move
			System.out.println("Cannot select a Chip that isn't yours to make a Move: (" + useX + ", " + useY + ")");
			return false;
		}
		
		if((useX - goX == 0) || (useY - goY == 0) || (useX - goX == useY - goY)){ // Checks directional selection of the Move; can only be Vertical, Horizontal, or True Diagonal
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

		//TODO: Check for the currentPlayer's Chip on the other Side
		
		if(vert){
			for(int i = Math.min(useY, goY); i < Math.max(useY, goY); i++){
				boardState[useX][i] = currentPlayer;
			}
		}else if(hori){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				boardState[i][useY] = currentPlayer;
			}
		}else if(tl_br_diag){
			for(int i = Math.min(useX, goX); i < Math.max(useX, goX); i++){
				for(int j = Math.min(useY, goY); j < Math.max(useY, goY); j++){
					boardState[i][j] = currentPlayer;
				}
			}
		}else if(bl_tr_diag){
			//TODO: DIAGONALS!!!
		}
		
		// Survived the Trials of Logic; Valid Move
		currentPlayer = (currentPlayer + 1) % 2; // Switches the currentPlayer
		return true;
	}
	
	@Override
	public <T extends BoardGameState> List<T> possibleBoardGameStates(T currentState) {
		
		List<T> possible = new ArrayList<T>();
		List<Point> chipMoves = new ArrayList<Point>();
		List<Point> emptySpaces = new ArrayList<Point>();
		
		for(int i = 0; i < BOARD_WIDTH; i++){
			for(int j = 0; j < BOARD_WIDTH; j++){
				if(boardState[i][j] == currentPlayer){
					chipMoves.add(new Point(i, j));
				}else if(boardState[i][j] == EMPTY){
					emptySpaces.add(new Point(i, j));
				}
			}
		}
		
		for(Point chip : chipMoves){
			for(Point empty : emptySpaces){
				@SuppressWarnings("unchecked")
				T temp = (T) copy(); // Creates a new Copy each time
				if(move(chip, empty)){ // Valid Move using chip and empty
					possible.add(temp); // Makes the Move in the Check
				}
			}
		}
		
		return possible;
	}

	@Override
	public BoardGameState copy() {
		OthelloState temp = new OthelloState(boardState, currentPlayer, winners);
		return temp;
	}

	@Override
	public List<Integer> getWinner() {
		return winners;
	}

	public String toString(){
		String result = " _ _ _ _ _ _ _ _ ";
		for(int i = 0; i < BOARD_WIDTH; i++){
			result += "\n|";
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
			result += "\n _ _ _ _ _ _ _ _";
		}
		
		return result;
	}
	
}
