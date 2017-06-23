package boardGame.fitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.heuristics.BoardGameHeuristic;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class HallOfFameFitness<T extends Network, S extends BoardGameState> implements BoardGameFitnessFunction<S> {
	
	BoardGameFitnessFunction<S> selectionFunction;
	int currentGen = -1;
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	BoardGameFeatureExtractor<S> featExtract;
	
	HeuristicBoardGamePlayer<S> champ;
	
	Map<Long, Double> evaluated = new HashMap<Long, Double>();
	
	@SuppressWarnings("unchecked")
	public HallOfFameFitness(){
		try {
			champ = (HeuristicBoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public double getFitness(BoardGamePlayer<S> player, int index) {
		long genotypeID = -1;
		BoardGameHeuristic<S> bgh;
		
		if(player instanceof HeuristicBoardGamePlayer){
			bgh = ((HeuristicBoardGamePlayer<S>) player).getHeuristic();
			if(bgh instanceof NNBoardGameHeuristic){
				genotypeID = ((NNBoardGameHeuristic<?,S>) bgh).getID();
			} else {
				return 0; // Don't have static opponents play against other static opponents
			}
		} else {
			return 0; // Don't have static opponents play against other static opponents
		}
		
		if(evaluated.containsKey(genotypeID)){
			return evaluated.get(genotypeID);
		}else if(MMNEAT.ea.currentGeneration() >= 1){ // Must complete at least one full Generation
			
			Pair<double[], double[]> evalResults = MMNEAT.hall.eval(((NNBoardGameHeuristic<?,S>) bgh).getGenotype());
			double score = evalResults.t1[0]; // Only uses 1 Selection Function
			
			evaluated.put(genotypeID, score);
			
			return score;
		}else{
			return 0.0; // Did not complete a full Generation yet
		}
	}

	@Override
	public void updateFitness(S bgs, int index) {
		// Doesn't update until getFitness
	}

	@Override
	public String getFitnessName() {
		return "Hall Of Fame Fitness";
	}

	@Override
	public void reset() {
		int testGen = MMNEAT.ea.currentGeneration();
		 
		 if(currentGen != testGen){
			evaluated.clear(); 
			currentGen = testGen;
		 }
	}

}
