package boardGame.fitnessFunction;

import java.util.List;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public class SimpleWinLoseDrawBoardGameFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T>{
	
	private double fitness = 0;
	
	@Override
	public double getFitness(BoardGamePlayer<T> player){
		return fitness;
	}
	
	@Override
	public void updateFitness(T bgs, int index) {
		List<Integer> winners = bgs.getWinners();
		
		if(winners.size() >=1){ // Game has reached an EndState; can eval the final State
			fitness = winners.size() > 1 && winners.contains(index) ? 0 : // multiple winners means tie: fitness is 0 
				(winners.get(0) == index ? 1 // If the one winner is 0, then the neural network won: fitness 1
									 : -2); // Else the network lost: fitness -2
		}
	}

	@Override
	public String getFitnessName() {
		return "Simple Win-Lose-Draw";
	}

	@Override
	public void reset() {
		fitness = 0;
	}
	
}
