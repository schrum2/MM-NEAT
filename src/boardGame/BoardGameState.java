package boardGame;

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
	
}
