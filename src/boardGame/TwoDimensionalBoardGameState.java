package boardGame;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class TwoDimensionalBoardGameState implements BoardGameState {

	public static final int EMPTY = -1;

	protected int[][] boardState;
	protected int nextPlayer;
	protected List<Integer> winners;
	protected final int numPlayers;

	public TwoDimensionalBoardGameState(int numPlayers) {
		this.numPlayers = numPlayers;
		nextPlayer = 0;
		winners = new LinkedList<Integer>();
		emptyBoard();
		setupStartingBoard();
	}
	
	/**
	 * Copy constructor
	 * @param state Another TwoDimensionalBoardGameState
	 */
	public TwoDimensionalBoardGameState(TwoDimensionalBoardGameState state) {
		this.nextPlayer = state.nextPlayer;
		this.numPlayers = state.numPlayers;
		// Clear board
		emptyBoard();
		// Fill with symbols from state
		for(int i = 0; i < getBoardWidth(); i++){
			for(int j = 0; j < getBoardHeight(); j++){
				boardState[i][j] = state.boardState[i][j];
			}
		}
		winners = new LinkedList<>(); 
		winners.addAll(state.winners);
	}	
	
	/**
	 * Constructor used for Testing; allow the User to individually control the aspects of the BoardState
	 * 
	 * @param board
	 * @param numPlay
	 * @param player
	 * @param win
	 */
	protected TwoDimensionalBoardGameState(int[][] board, int numPlay, int player, List<Integer> win){
		assert board.length == getBoardHeight();
		
		for(int i = 0; i < board.length; i++){
			assert board[i].length == getBoardWidth();
		}
		
		boardState = board;
		numPlayers = numPlay;
		nextPlayer = player;
		winners = win;
	}
	
	/**
	 * Get number of players in the game
	 * @return number of players
	 */
	public int getNumPlayers() {
		return this.numPlayers;
	}
	
	/**
	 * Empties all board squares
	 */
	protected void emptyBoard() {
		boardState = new int[getBoardWidth()][getBoardHeight()];
		for(int i = 0; i < boardState.length; i++) {
			for(int j = 0; j < boardState[0].length; j++) {
				boardState[i][j] = EMPTY;
			}
		}
	}

	/**
	 * Places pieces on board as they will be at the start of a game
	 */
	public abstract void setupStartingBoard();

	public abstract int getBoardWidth();

	public abstract int getBoardHeight();
	
	/**
	 * Returns the Index of the next Player
	 * 
	 * @return Index of the next Player
	 */
	public int getNextPlayer() {
		return nextPlayer;
	}

	/**
	 * Returns the representation of the indexes of empty Spaces as Points
	 * 
	 * @return List<Point> of the empty Spaces in the Tic-Tac-Toe Board
	 */
	public List<Point> getEmptyIndex(){
		List<Point> indexes = new LinkedList<Point>();
		
		for(int i = 0; i < getBoardWidth(); i++){
			for(int j = 0; j < getBoardHeight(); j++){
				if(boardState[i][j] == EMPTY){
					indexes.add(new Point(i, j));
				}
			}
		}
		
		return indexes;
	}

	/**
	 * Place marker associated with player at the given point on the board,
	 * and return true if successful. Return false if the space was already
	 * occupied.
	 * @param player Integer player index
	 * @param space x/y coordinate on board
	 * @return whether placement is successful
	 */
	public boolean placePlayerPiece(int player, Point space) {
		if(boardState[space.x][space.y] == EMPTY) {
			boardState[space.x][space.y] = player;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Move a player piece from one location to another space, if possible, and return true.
	 * Otherwise return false. If the destination is occupied, allowCapture must be true in
	 * order for the move to be successful. Technically, this method allows self capture.
	 * 
	 * @param source Where piece is being moved from (should contain a piece)
	 * @param destination empty space to move piece to (should be empty)
	 * @param allowCapture whether the destination space can be occupied (its piece will be removed)
	 * @return whether successful
	 */
	public boolean movePlayerPiece(Point source, Point destination, boolean allowCapture) {
		if(boardState[source.x][source.y] != EMPTY && (allowCapture || boardState[destination.x][destination.y] == EMPTY)) {
			boardState[destination.x][destination.y] = boardState[source.x][source.y]; // move to new destination
			boardState[source.x][source.y] = EMPTY; // remove from original space
			return true;
		} else {
			return false;
		}		
	}
	
	/**
	 * Removes a Piece on the Board at a specific Point.
	 * The Space where the Piece was will be Empty
	 * 
	 * @param location Point representing where the Piece to be Removed is
	 * @return True if the remove was successful, else returns false
	 */
	public boolean removePiece(Point location){
		assert (location.getX() >= 0 && location.getX() < getBoardWidth());
		assert (location.getX() >= 0 && location.getX() < getBoardHeight());

		if((location.getX() >= 0 && location.getX() < getBoardWidth()) &&
				(location.getX() >= 0 && location.getX() < getBoardHeight())){
			boardState[(int) location.getX()][(int) location.getY()] = EMPTY;
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Returns the Index of the winning Player
	 * 
	 * @return ArrayList<Integer> containing the winners
	 */
	public List<Integer> getWinners(){
		return winners;
	}

	/**
	 * Array that associates a char with each player index for a String-based representation
	 * @return array with a char for each player of game
	 */
	public abstract char[] getPlayerSymbols();
	
	public abstract Color[] getPlayerColors();
	
	/**
	 * Prints out a visual representation of the TicTacToeState to the console
	 */
	public String toString(){
		String result = "Next Player: " + nextPlayer + " numPlayers: " + numPlayers + " winners: " + winners;
		result += "\n  0 1 2 3 4 5 6 7\n ";
		for(int i = 0; i < getBoardWidth(); i++) {
			result += "--";
		}
		result += "-\n";
		
		for(int i = 0; i < getBoardHeight(); i++){
			result += i + "|";
			
			for(int j = 0; j < getBoardWidth(); j++){
				
				char mark;
				int space = boardState[i][j];
				
				if(space == EMPTY){
					mark = ' ';
				}else{
					mark = getPlayerSymbols()[space];
				}
				result += mark + "|";
			}
			
			result += "\n -";
			for(int k = 0; k < getBoardWidth(); k++) {
				result += "--";
			}
			result += "\n";
		}
		result += "\n\n";
		return result;
	}

	/**
	 * Returns true if a given Point refers to a Space within the Bounds of the Board,
	 * Else returns false
	 * 
	 * @param source Point to be evaluated
	 * @return True if the Point is In-Bounds, else returns false
	 */
	public boolean isPointInBounds(Point source){
		return ((source.getX() >= 0 && source.getX() < getBoardWidth()) && (source.getY() >= 0 && source.getY() < getBoardHeight()));
	}
	
	
	public int numberOfPieces(int playerIndex){
		int numPieces = 0;
		
		for(int i = 0; i < getBoardWidth(); i++){
			for(int j = 0; j < getBoardHeight(); j++){
				if(boardState[i][j] == playerIndex) numPieces++;
			}
		}
		
		return numPieces;
	}
	
	/**
	 * Returns an Array of Doubles that describes this BoardGameState
	 * 
	 * @return double[] describing this BoardGameState
	 */
	@Override
	public double[] getDescriptor() {

		double[] features = new double[getBoardWidth()*getBoardHeight()];
		int index = 0;
		
		// This method will not work with BoardGames with more than 2 Players TODO: Generalize this?
		for(int i = 0; i < getBoardHeight(); i++){
			for(int j = 0; j < getBoardWidth(); j ++){
				switch(boardState[i][j]){
				case EMPTY: features[index++] = 0; break; // Empty Space
				case 0: features[index++] = 1; break; // Player 1 = +1
				case 1: features[index++] = -1; break; // Player 2 = -1
				}
				
				
			}
		}
		
		return features;
	}
	
	/**
	 * Auto-generated hash code method
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(boardState);
		result = prime * result + nextPlayer;
		result = prime * result + numPlayers;
		result = prime * result + ((winners == null) ? 0 : winners.hashCode());
		return result;
	}

	
	public boolean equals(Object other){
		
		TwoDimensionalBoardGameState other2D = (TwoDimensionalBoardGameState) other;
		
		if(this.getBoardWidth() != other2D.getBoardWidth() || this.getBoardHeight() != other2D.getBoardHeight()) return false;
		
		for(int i = 0; i < getBoardWidth(); i++){
			for(int j = 0; j < getBoardHeight(); j++){
				if(this.boardState[i][j] != other2D.boardState[i][j]){
					return false;
				}
			}
		}
		
		if(this.nextPlayer != other2D.nextPlayer || !(this.winners.equals(other2D.winners) || this.numPlayers != other2D.numPlayers)) return false;
		
		return true;
	}
	
}
