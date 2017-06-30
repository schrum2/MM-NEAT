package boardGame.connectFour;

import java.awt.Color;
import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGameState;

public class ConnectFourState extends TwoDimensionalBoardGameState{
	
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private static final int BLACK_CHECK = 0;
	private static final int RED_CHECK = 1;
	private static final int NUMBER_OF_PLAYERS = 2;
	
	public ConnectFourState(){
		super(2);
	}

	public ConnectFourState(ConnectFourState state) {
		super(state);
	}

	public ConnectFourState(int[][] board, int nextPlay, List<Integer> win){
		super(board, NUMBER_OF_PLAYERS, nextPlay, win);
	}
	
	@Override
	public boolean endState() {
		int check = EMPTY;
		
		for(int i = 0; i < WIDTH; i++){
			for(int j = 0; j < HEIGHT; j++){
				
				if(boardState[i][j] == EMPTY){
					continue; // Move on to the next Point
				}else{
					// Cycles through the possible displacements
					for(int dX = -1; dX <= 1; dX++){
						for(int dY = -1; dY <=1; dY++){
							
							// Only runs if there is a displacement in any direction
							// and if there is a Check at the selected Space
							if(dX != 0 && dY != 0 && boardState[i][j] != EMPTY){
								int numInRow = 0;
								check = boardState[i][j];
								
								int x = i;
								int y = j;
								
								do{
									x = i + dX;
									y = j + dY;
									
									if(((x >= 0 && x < WIDTH) && (y >= 0 && y < HEIGHT)) && boardState[x][y] == check) numInRow++;
								}while(((x >= 0 && x < WIDTH) && (y >= 0 && y < HEIGHT)) && boardState[x][y] == check || numInRow < 4);
								if(numInRow >= 4){
									System.out.println("Ended because there was a Four-In-A-Row: " + check);
									winners.add(check);
									return true;
								}
							}
							check = EMPTY; // Resets the check
						}
					}
				}
			}
		}
		
		// Unable to find a Four-In-A-Row; see if the Board is full
		Set<ConnectFourState> poss = possibleBoardGameStates(this);
		if(poss.size() == 0){
			System.out.println("Ended due to no more Moves left");
			winners.add(BLACK_CHECK);
			winners.add(RED_CHECK);
			return true;
		}
		
		// Able to make at least one Move; game is not over
		System.out.println("Checked End State: False");
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BoardGameState> Set<T> possibleBoardGameStates(T currentState) {
		Set<T> poss = new HashSet<T>();
		System.out.println("Checking Possible States");
		for(int i = 0; i < WIDTH; i++){
			ConnectFourState temp = this.copy();
			if(boardState[i][0] == EMPTY){
				temp.moveSinglePoint(new Point(i, 0));
				poss.add((T) temp);
			}
		}
		
		return poss;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BoardGameState> T copy() {
		return (T) new ConnectFourState(this);
	}
	
	@Override
	public List<Integer> getWinners() {
		return winners;
	}
	
	@Override
	public void setupStartingBoard() {
		boardState = new int[WIDTH][HEIGHT];
		
		for(int i = 0; i < WIDTH; i++){
			for(int j = 0; j < HEIGHT; j++){
				boardState[i][j] = EMPTY;
			}
		}
	}

	@Override
	public int getBoardWidth() {
		return WIDTH;
	}

	@Override
	public int getBoardHeight() {
		return HEIGHT;
	}

	@Override
	public char[] getPlayerSymbols() {
		return new char[]{'R', 'B'};
	}

	@Override
	public Color[] getPlayerColors() {
		return new Color[]{Color.black, Color.red};
	}

	@Override
	public boolean moveOnePiece() {
		return true;
	}

	@Override
	public boolean moveSinglePoint(Point goTo) {
		int pointX = (int) goTo.getX();
		int pointY = (int) goTo.getY();

		// Must select an empty Space from the top of the Board
		if(boardState[pointX][pointY] != -1 || pointY != 0){
			return false;
		}else{
			int height = 0;
			for(int i = 0; i < HEIGHT; i++){
				if(boardState[pointX][i] == -1) height = i;
			}
			boardState[pointX][height] = nextPlayer;
			System.out.println("Player " + nextPlayer + " made a Move to: (" + pointX + ", " + height + ")\n" + toString());
			nextPlayer = (nextPlayer + 1) % NUMBER_OF_PLAYERS;
			return true;
		}
	}

	@Override
	public boolean moveDoublePoint(Point goTo, Point moveTo) {
		// Never used by this domain; always returns false
		return false;
	}
	
}
