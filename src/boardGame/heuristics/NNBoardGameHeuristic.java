package boardGame.heuristics;

import boardGame.BoardGameState;
import edu.utexas.cs.nn.networks.Network;

public class NNBoardGameHeuristic<T extends Network, S extends BoardGameState> implements BoardGameHeuristic<S> {

	T network;
	
	public NNBoardGameHeuristic(T net){
		network = net;
	}
	
	public NNBoardGameHeuristic(){ // Used as a blank constructor; the Network can be set in the BoardGameTasks
	}
	
	@Override
	public double heuristicEvalution(S current) {
		return network.process(current.getDescriptor())[0]; // Returns the Network's Score for the current BoardGameState's descriptor
	}

}