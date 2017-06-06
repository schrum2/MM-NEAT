package boardGame.fitnessFunction;

import boardGame.BoardGameState;

public interface BoardGameFitnessFunction<T extends BoardGameState> {
	
	public double getFitness(T bgs, int index);
	
}
