package boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.cs.nn.networks.Network;

public class BoardGamePlayerOneStepEval<T extends Network> implements BoardGamePlayer {

	T network;
	
	public BoardGamePlayerOneStepEval(T net){
		network = net;
	}
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
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
