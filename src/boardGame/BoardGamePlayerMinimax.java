package boardGame;

import java.util.ArrayList;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGamePlayerMinimax<T extends Network,S extends BoardGameState> implements BoardGamePlayer<S> {
	
	T network; // Remove eventually
	BoardGameHeuristic boardHeuristic; // Should generalize to take any heuristic function, not just a network eval
	
	
	public BoardGamePlayerMinimax(T net, BoardGameHeuristic bgh){
		network = net; // Remove eventually
		boardHeuristic = bgh;
	}	
	
	@Override
	public S takeAction(S current) {
		// TODO: Add Heuristic analysis; Right now, has exact same code as OneStepEval Player
		
		ArrayList<S> poss = new ArrayList<>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()];
		
		for(int i = 0; i < utilities.length; i++){ // Gets the network's outputs for all possible BoardGameStates
			double[] description = poss.get(i).getDescriptor();
			utilities[i] = network.process(description)[0]; // utility score: replace with call to heuristic function
		}

		return poss.get(StatisticsUtilities.argmax(utilities));
	}

}
