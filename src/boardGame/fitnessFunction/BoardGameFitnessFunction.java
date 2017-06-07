package boardGame.fitnessFunction;

import boardGame.BoardGameState;

public interface BoardGameFitnessFunction<T extends BoardGameState> {
	
	public double updateFitness(T bgs, int index);
	
}