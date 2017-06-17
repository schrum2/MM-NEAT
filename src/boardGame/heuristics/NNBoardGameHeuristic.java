package boardGame.heuristics;

import java.util.List;

import boardGame.BoardGameState;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.Parameters;

public class NNBoardGameHeuristic<T extends Network, S extends BoardGameState> implements BoardGameHeuristic<S> {

	long ID;
	T network;
	BoardGameFeatureExtractor<S> featExtract;
	
	public NNBoardGameHeuristic(long genotypeID, T net, BoardGameFeatureExtractor<S> fe){
		ID = genotypeID;
		network = net;
		featExtract = fe;
	}
	
	public NNBoardGameHeuristic(){ // Used as a blank constructor; the Network can be set in the BoardGameTasks
	}
	
	public long getID(){
		return ID;
	}
	
	@Override
	public double heuristicEvalution(S current) {
//		if(Parameters.parameters.booleanParameter("stepByStep")){
//			System.out.print("Press enter to continue");
//			System.out.println(current);
//			MiscUtil.waitForReadStringAndEnterKeyPress();
//		}
		if(Parameters.parameters.booleanParameter("heuristicOverrideTerminalStates")){ // Overrides the Network's evaluation if set to True
			if(current.endState()){
				List<Integer> winners = current.getWinners();

				if(winners.size() == 1 && winners.contains(0)){ // Player 1 is only winner
					return 1;
				} else if(winners.size() == 1 && winners.contains(1)){ // Player 2 is only winner
					return -1;
				} else if(winners.size() > 1){ // More than one Player wins, considered a Tie
					return 0;
				} else{  
					throw new IllegalStateException("This heuristic is currently only capable of handling two-player games");
				}

			}else{
				network.flush(); // wipe out recurrent activations
				return network.process(featExtract.getFeatures(current))[0]; // Returns the Network's Score for the current BoardGameState's descriptor
			}
		}
		return network.process(featExtract.getFeatures(current))[0]; // Returns the Network's Score for the current BoardGameState's descriptor
		
	}

}