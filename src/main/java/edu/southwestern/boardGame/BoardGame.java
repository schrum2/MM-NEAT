package edu.southwestern.boardGame;

import java.util.List;

import edu.southwestern.boardGame.agents.BoardGamePlayer;

/**
 * Generic Interface for a simulated Board Game
 * 
 * @author johnso17
 */
public interface BoardGame<T extends BoardGameState> {
	
	/**
	 * Returns the number of Players in a specific BoardGame
	 * 
	 * @return Number of Players
	 */
	public int getNumPlayers();
	
	/**
	 * Returns true if the BoardGame reached an end state, else returns false
	 * 
	 * @return True if the BoardGame ended, else returns false
	 */
	public boolean isGameOver();
	
	public double[] getDescription();
	
	/**
	 * Returns a List of all indexes of the winners of a BoardGame
	 * 
	 * @return The indexes of the winners of a BoardGame
	 */
	public List<Integer> getWinners();
	
	/**
	 * Updates the BoardGameState based on the action taken by a given BoardGamePlayer
	 * 
	 * @param bgp Player that gets to move next
	 */
	public void move(BoardGamePlayer<T> bgp);
	
	
	/**
	 * Returns the index of the BoardGamePlayer which will be updating the BoardGameState
	 * 
	 * @return index of player updating the BoardGameState next
	 */
	public int getCurrentPlayer();

	public BoardGameState getCurrentState();
	
	/**
	 * Returns the name of the BoardGame
	 * 
	 * @return String containing the name of the BoardGame
	 */
	public String getName();


	
	/**
	 * Resets the current BoardGame
	 */
	public void reset();
}
