package boardGame.fitnessFunction;

import boardGame.BoardGameState;

public interface BoardGameFitnessFunction<T extends BoardGameState> {
	
	/**
	 * Returns the Fitness of a given Board Game Agent after a single Board Game
	 * 
	 * @return Double representing the Fitness of a given Board Game Agent
	 */
	public double getFitness();
	
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