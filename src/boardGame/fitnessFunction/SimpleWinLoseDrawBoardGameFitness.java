package boardGame.fitnessFunction;

import java.util.List;

import boardGame.BoardGameState;

public class SimpleWinLoseDrawBoardGameFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T>{

	@Override
	public double updateFitness(T bgs, int index) {
		List<Integer> winners = bgs.getWinners();
		
		if(winners.size() >=1){ // Game has reached an EndState; can eval the final State
			double fitness = winners.size() > 1 && winners.contains(index) ? 0 : // multiple winners means tie: fitness is 0 
				(winners.get(0) == index ? 1 // If the one winner is 0, then the neural network won: fitness 1
									 : -2); // Else the network lost: fitness -2
			
			return fitness;
		}else{ // Game is not over; this Fitness Function evals all Non-End States equally
			return 0;
		}
	}

	@Override
	public String getFitnessName() {
		return "Simple Win-Lose-Draw";
	}
	
}
