package boardGame;

import edu.utexas.cs.nn.networks.Network;

public class NNBoardGameHeuristic<T extends Network, S extends BoardGameState> implements BoardGameHeuristic<S> {

	T network;
	
	public NNBoardGameHeuristic(T net){
		network = net;
	}
	
	@Override
	public double heuristicEvalution(S current) {
		return network.process(current.getDescriptor())[0]; // Returns the Network's Score for the current BoardGameState's descriptor
	}

}