package boardGame.rps;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import boardGame.BoardGameState;

public class RPSState implements BoardGameState{
	// package private
	int[] playerMoves;
	int nextPlayer;
		
	public static final int TIE = 0;
	public static final int PLAYER1 = 1;
	public static final int PLAYER1_INDEX = 0;
	public static final int PLAYER2 = 2;	
	public static final int PLAYER2_INDEX = 1;	

	
	public static final int UNDECIDED = -1;
	public static final int ROCK = 0;
	public static final int PAPER = 1;
	public static final int SCISSORS = 2;	
	
	private List<Integer> winner;
	private int currentPlayer;
	
	/**
	 * Default Constructor
	 */
	public RPSState(){
		playerMoves = new int[]{UNDECIDED, UNDECIDED};
		nextPlayer = 0;
		winner = new ArrayList<Integer>();
		currentPlayer = PLAYER1;
	}
	
	/**
	 * Private Constructor; Allows a User to directly enter the Moves for the Rock Paper Scissors Game
	 * 
	 * @param newMoves Should be an int[2] with values of 0 (Rock), 1 (Paper), or 2 (Scissors) only. Sets the Moves for the RPSState.
	 */
	private RPSState(int[] newMoves){
		// Checks for the correct amount of Moves
		if(newMoves.length != 2){
			throw new IllegalArgumentException("Added too many Moves: " + newMoves.length + ". Can only have 2 Moves (int[] can only have a Length of 2).");
		}else{
			// Checks that the Moves are correct
			if(newMoves[0] != ROCK || newMoves[0] != PAPER || newMoves[0] != SCISSORS){
				throw new IllegalArgumentException("Incorrect Move for first Move: " + newMoves[0] + ". Moves can only be 0 (Rock), 1 (Paper), or 2 (Scissors).");
			}else if(newMoves[1] != ROCK || newMoves[1] != PAPER || newMoves[1] != SCISSORS){
				throw new IllegalArgumentException("Incorrect Move for second Move: " + newMoves[0] + ". Moves can only be 0 (Rock), 1 (Paper), or 2 (Scissors).");
			}else{
				// Correct number of Moves and correct Moves
				playerMoves = newMoves;
			}
		}
	}
	
	/**
	 * Returns a Array containing Doubles that represent the BoardGameState
	 * 
	 * @return double[] representing the BoardGameState
	 */
	@Override
	public double[] getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns a List of Integers representing the possible Moves the Player could take
	 * 
	 * @return List<Integer> with 0 (Rock), 1 (Paper), and 2 (Scissors)
	 */
	public List<Integer> getPossibleMoves(){
		List<Integer> moves = new ArrayList<Integer>();
		moves.add((Integer) ROCK);
		moves.add((Integer) PAPER);
		moves.add((Integer) SCISSORS);
		return moves;
	}
	
	/**
	 * Returns an int representing the outcome of the Rock-Paper-Scissors match
	 * 
	 * @return -1 if the game is Undecided, 0 if there was a Tie, 1 if Player 1 wins, or 2 if Player 2 wins
	 */
	public List<Integer> getWinners(){
		if(endState()){
			if((playerMoves[0] == ROCK && playerMoves[1] == SCISSORS) || (playerMoves[0] == PAPER && playerMoves[1] == ROCK) || (playerMoves[0] == SCISSORS && playerMoves[1] == PAPER)){ // Player 1 wins
				winner.add(PLAYER1);
			}else if ((playerMoves[1] == ROCK && playerMoves[0] == SCISSORS) || (playerMoves[1] == PAPER && playerMoves[0] == ROCK) || (playerMoves[1] == SCISSORS && playerMoves[0] == PAPER)){ // Player 2 wins
				winner.add(PLAYER2);
			}else{ // Tie
				winner.add(TIE);
			}
		}
		return winner;
	}

	/**
	 * Sets the Player choice to a specified Move
	 * 
	 * @param player Inex representing the Player making a Move
	 * @param move Integer representing the Move that Player is making
	 */
	public void chooseMove(int move){
		assert move >= 0 && move < 3; // Ensures the Move is correct
		if(currentPlayer == PLAYER1){
			playerMoves[PLAYER1_INDEX] = move;
			currentPlayer = PLAYER2;
		}else{
			playerMoves[PLAYER2_INDEX] = move;			
		}
	}
	
	/**
	 * Returns true if both Player Moves have been decided, else returns false
	 * 
	 * @return True if both Players have made a Move, else returns false
	 */
	@Override
	public boolean endState() {
		boolean over = (playerMoves[0] != UNDECIDED && playerMoves[1] != UNDECIDED);
		if(over){
			getWinners(); // Sets the global variable "winner"
		}
		return over;
	}

	/**
	 * Returns a String representing the results of a Rock-Paper-Scissors match
	 * 
	 * @return String containing the results of a given Rock-Paper-Scissors match
	 */
	public String toString() {
		if(endState()) {
			return label(playerMoves[0]) + " vs. " + label(playerMoves[1]);
		} else {
			return "Result Pending";
		}
	}

	/**
	 * Returns a String containing the Rock Paper Scissors representation of that choice
	 * 
	 * @param choice int representing a Player choice
	 * @return String containing "Rock", "Paper", or "Scissors"
	 */
	private String label(int choice) {
		switch(choice) {
		case SCISSORS: return "Scissors";
		case ROCK: return "Rock";
		case PAPER: return "Paper";
		}
		throw new IllegalArgumentException("Must choose Rock, Paper, or Scissors, not " + choice);
	}

	/**
	 * Creates a Duplicate of this BoardGameState and returns it
	 * 
	 * @return BoardGameState Duplicate of this BoardGameState
	 */
	@Override
	public BoardGameState copy() {
		RPSState temp = new RPSState(playerMoves);
		return temp;
	}

	/**
	 * Returns a List of all possible BoardGameStates starting from a given BoardGameState
	 * 
	 * @param player Index representing a Player to take action
	 * @param currentState Current BoardGameState being used
	 * @return List<BoardGameState> with all BoardGameStates possible from a given BoardGameState
	 */
	@Override
	public Set<BoardGameState> possibleBoardGameStates(BoardGameState currentState) {
		Set<BoardGameState> returnStates = new HashSet<BoardGameState>();
		List<Integer> tempMoves = getPossibleMoves();
		
		for(Integer i : tempMoves){
			RPSState tempState = (RPSState) currentState.copy();
			tempState.chooseMove(i);
			returnStates.add(tempState);
		}
		
		return returnStates;
	}

	@Override
	public int getNumPlayers() {
		// TODO Auto-generated method stub
		return 2;
	}
	
}
