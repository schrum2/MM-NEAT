package boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.stats.StatisticsUtilities;

public class BoardGamePlayerOneStepEval<T extends Network> implements BoardGamePlayer<BoardGameState> {

	T network;
	
	public BoardGamePlayerOneStepEval(T net){
		network = net;
	}
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		List<BoardGameState> poss = new ArrayList<BoardGameState>();
		poss.addAll(current.possibleBoardGameStates(current));
		double[] utilities = new double[poss.size()]; // Stores the network's ouputs
		
		int index = 0;
		for(BoardGameState bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			double[] description = bgs.getDescriptor();
			utilities[index++] = network.process(description)[0];
		}

		return poss.get(StatisticsUtilities.argmax(utilities)); // Returns the BoardGameState which produced the highest network output
	}


}
