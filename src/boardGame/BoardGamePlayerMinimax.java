package boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGamePlayerMinimax<T extends BoardGameState, S extends BoardGameHeuristic<T>> implements BoardGamePlayer<T> {
	
	S boardHeuristic; // Should generalize to take any heuristic function, not just a network eval
	
	
	public BoardGamePlayerMinimax(T net, S bgh){
		boardHeuristic = bgh;
	}	
	
	@Override
	public T takeAction(T current) {
		List<T> poss = new ArrayList<T>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()]; // Stores the network's ouputs
		
		int index = 0;
		for(T bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			utilities[index++] = boardHeuristic.heuristicEvalution(bgs);
		}

		return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
	}

}
