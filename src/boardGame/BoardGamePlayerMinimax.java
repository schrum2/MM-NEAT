package boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.networks.Network;

public class BoardGamePlayerMinimax<T extends Network> implements BoardGamePlayer{
	
	T network;
	BoardGameHeuristic boardHeuristic;
	
	
	public BoardGamePlayerMinimax(T net, BoardGameHeuristic bgh){
		network = net;
		boardHeuristic = bgh;
	}	
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		// TODO: Add Heuristic analysis; Right now, has exact same code as OneStepEval Player
		List <double[]> outputs = new ArrayList<double[]>(); // Stores the network's ouputs
		List<BoardGameState> poss = current.possibleBoardGameStates(current);
		
		for(BoardGameState bgs : poss){ // Gets the network's outputs for all possible BoardGameStates
			double[] description = bgs.getDescriptor();
			outputs.add(network.process(description));
		}
		
		double[] action = new double[0]; // Stores the Array with the longest length
		
		for(double[] d : outputs){ // Gets the output with the longest Array length
			if(d.length > action.length){
				action = d;
			}
		}
		return poss.get(outputs.indexOf(action)); // Returns the BoardGameState which produced the highest network output
	}

}
