package boardGame;

import java.util.List;

/**
 * Generic Interface for a simulated Board Game
 * 
 * @author johnso17
 */
public interface BoardGame {
	
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
	
	
	
	/**
	 * Returns a List of all indexes of the winners of a BoardGame
	 * 
	 * @return The indexes of the winners of a BoardGame
	 */
	public List<Integer> getWinners();
	
	
	
	/**
	 * Returns a List of all the possible BoardGameStates when starting at a given BoardGameState
	 * 
	 * @param player Index indicating which player's turn it is
	 * @param currentState The current BoardGameState
	 * @return List<BoardGameState> of all possible BoardGameStates starting from the given currentState
	 */
	public List<BoardGameState> possibleBoardGameStates(int player, BoardGameState currentState);
	
	
	
	/**
	 * Updates the BoardGameState based on the action taken by a given BoardGamePlayer
	 * 
	 */
	public void move();
	
	
	/**
	 * Returns the BoardGamePlayer which will be updating the BoardGameState
	 * 
	 * @return The BoardGamePlayer updating the BoardGameState
	 */
	public BoardGamePlayer getCurrentPlayer();

	
	
	/**
	 * Returns the name of the BoardGame
	 * 
	 * @return String containing the name of the BoardGame
	 */
	public String getName();

}
