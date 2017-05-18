package boardGame.rps;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGameState;

public class RockPaperScissors implements BoardGame{

	private int currentPlayer; // Used to keep track of whose turn it is
	private RPSState board;
	
	/**
	 * Default Constructor; Cerates an empty game of Rock Paper Scissors
	 */
	public RockPaperScissors(){
		currentPlayer = 0;
		board = new RPSState();
	}
	
	/**
	 * Returns The number of Players of Rock Paper Scissors
	 * 
	 * @return 2, the number of Players
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns true if the Rock Paper Scissors game is over, else returns false
	 * 
	 * @return True if the game has reached an end state, else returns false
	 */
	@Override
	public boolean isGameOver() {
		return board.endState();
	}

	/**
	 * Returns a List with the Indexes of the winners of this Game
	 * 
	 * @return List<Integer> with the Indexes of all Game winners
	 */
	@Override
	public List<Integer> getWinners() {
		List<Integer> winners = new ArrayList<Integer>(board.getWinner());
		return winners;
	}

	/**
	 * Returns a List of all possible BoardGameStates starting from a given BoardGameState
	 * 
	 * @param player Index representing a Player to take action
	 * @param currentState Current BoardGameState being used
	 * @return List<BoardGameState> with all BoardGameStates possible from a given BoardGameState
	 */
	@Override
	public List<BoardGameState> possibleBoardGameStates(int player, BoardGameState currentState) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Updates the RPSState based on a Player's action
	 */
	@Override
	public void move(BoardGamePlayer bgp) {
		bgp.takeAction(board);
		currentPlayer++;
	}

	/**
	 * Returns the Index of the current Player
	 * 
	 * @return Index of the current Player
	 */
	@Override
	public int getCurrentPlayer() {
		return 	currentPlayer;
	}

	/**
	 * Returns the name of the game, "Rock-Paper-Scissors"
	 * 
	 * @return String containing "Rock-Paper-Scissors"
	 */
	@Override
	public String getName() {
		return "Rock-Paper-Scissors";
	}

	/**
	 * Returns a String visually representing the current BoardGameState
	 * 
	 * @return String visually representing the BoardGameState
	 */
	public String toString() {
		return board.toString();
	}
}
