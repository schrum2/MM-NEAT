package boardGame.fitnessFunction;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public interface BoardGameFitnessFunction<T extends BoardGameState> {
	
	/**
	 * Returns the Fitness of a given Board Game Agent after a single Board Game
	 * 
	 * @param player Player whose fitness is being calculated
	 * @param index Index of player
	 * @return Double representing the Fitness of a given Board Game Agent
	 */
	public double getFitness(BoardGamePlayer<T> player, int index);
	
	/**
	 * Updates a given Board Game Agent's Fitness over the course
	 * of one Board Game
	 * 
	 * @param bgs Updated Board Game State to be evaluated
	 * @param index Index representing the Board Game Agent to be evaluated
	 */
	public void updateFitness(T bgs, int index);
	
	/**
	 * Returns the name of the Fitness Function being used
	 * 
	 * @return String containing the name of the Fitness Function
	 */
	public String getFitnessName();
	
	/**
	 * Resets the Fitness Function to 0 to avoid
	 * accumulating scores over multiple generations
	 */
	public void reset();
}