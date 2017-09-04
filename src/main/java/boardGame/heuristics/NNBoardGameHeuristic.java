package boardGame.heuristics;

import java.util.List;

import boardGame.BoardGameState;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;

public class NNBoardGameHeuristic<T extends Network, S extends BoardGameState> implements BoardGameHeuristic<S> {

	long ID;
	T network;
	BoardGameFeatureExtractor<S> featExtract;
	Genotype<T> gene;

	public NNBoardGameHeuristic(long genotypeID, BoardGameFeatureExtractor<S> fe, Genotype<T> gene){
		ID = genotypeID;
		network = gene.getPhenotype();
		featExtract = fe;
		this.gene = gene;
	}

	// Why is this constructor here?
	public NNBoardGameHeuristic(){ // Used as a blank constructor; the Network can be set in the BoardGameTasks
	}

	public long getID(){
		return ID;
	}

	public String toString() {
		return this.getClass().getSimpleName() + ", ID = " + ID;
	}

	public Genotype<T> getGenotype(){
		return gene;
	}

	@Override
	public double heuristicEvalution(S current) {
		//		if(Parameters.parameters.booleanParameter("stepByStep")){
		//			System.out.print("Press enter to continue");
		//			System.out.println(current);
		//			MiscUtil.waitForReadStringAndEnterKeyPress();
		//		}
		if(Parameters.parameters.booleanParameter("heuristicOverrideTerminalStates") && current.endState()){ // Overrides the Network's evaluation if set to True
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
			double utility = network.process(featExtract.getFeatures(current))[0]; // Returns the Network's Score for the current BoardGameState's descriptor
			assert utility >= -1 && utility <= 1 : "Utility out of range -1 to 1: " + utility;
			return utility;
		}
	}

}