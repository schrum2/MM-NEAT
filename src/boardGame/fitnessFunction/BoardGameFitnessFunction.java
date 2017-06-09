package boardGame.fitnessFunction;

import boardGame.BoardGameState;

public interface BoardGameFitnessFunction<T extends BoardGameState> {
	
	public double getFitness();
	
	public void updateFitness(T bgs, int index);
	
	public String getFitnessName();
}