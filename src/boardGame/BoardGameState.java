package boardGame;

import java.util.List;

/**
 * Generic Interface for simulated Board Game State
 * 
 * @author johnso17
 */
public interface BoardGameState {
	
	/**
	 * Returns an Array of Doubles describing the BoardGameState
	 * 
	 * @return double[] that describes the current BoardGameState
	 */
	public double[] getDescriptor();
	
	
	
	/**
	 * Returns true if the game has reached an end, else returns false
	 * 
	 * @return True if the BoardGame has reached an ending, else returns false
	 */
	public boolean endState();
	
	
	
	/**
	 * Returns a List of all the possible BoardGameStates when starting at a given BoardGameState
	 * 
	 * @param player Index indicating which player's turn it is
	 * @param currentState The current BoardGameState
	 * @return List<BoardGameState> of all possible BoardGameStates starting from the given currentState
	 */
	public <T extends BoardGameState> List<T> possibleBoardGameStates(T currentState);
	
	
	
	/**
	 * Returns a Duplicate of the current BoardGameState
	 * 
	 * @return Duplicate of this BoardGameState
	 */
	public BoardGameState copy();
}
