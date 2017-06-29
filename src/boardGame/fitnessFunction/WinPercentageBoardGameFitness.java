package boardGame.fitnessFunction;

import java.util.LinkedList;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;

public class WinPercentageBoardGameFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T>{
	
	private List<Integer> winners = new LinkedList<>();

	@Override
	public double getFitness(BoardGamePlayer<T> player, int index){
		if(winners.size() >=1){ // Game has reached an EndState; can eval the final State
			if(winners.size() > 1 && winners.contains(index)) return 0; // multiple winners means tie: fitness is 0 
			else if(winners.get(0) == index) return 1; // If the one winner is 0, then the neural network won: fitness 1
			else return 0; // Else the network lost: fitness -2
		}
		return 0; // No winners; game probably not even over ... should not happen
	}
	
	@Override
	public void updateFitness(T bgs, int index) {
		winners = bgs.getWinners();
	}

	@Override
	public String getFitnessName() {
		return "Win Percentage";
	}

	@Override
	public void reset() {
		winners = new LinkedList<>(); // no winners or losers
	}

	@Override
	public double getMinScore() {
		return 0; // Lowest possible score
	}
	
}
