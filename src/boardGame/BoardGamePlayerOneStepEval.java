package boardGame;

import java.util.List;

import edu.utexas.cs.nn.networks.Network;

public class BoardGamePlayerOneStepEval implements BoardGamePlayer {

	public BoardGamePlayerOneStepEval(){
	}
	
	@Override
	public BoardGameState takeAction(BoardGameState current) {
		// TODO Auto-generated method stub
		List<BoardGameState> poss = current.possibleBoardGameStates(current);
		for(BoardGameState bgs : poss){
			double[] description = bgs.getDescriptor();
			//network.process(description);
		}
		return null;
	}

}
