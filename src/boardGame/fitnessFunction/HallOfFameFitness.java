package boardGame.fitnessFunction;

import java.util.HashMap;
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
	
	private boolean currentlyEvaluatingHallOfFame = false;
	int currentGen = -1;
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
		// Do not evaluate in infinite loop
		if(currentlyEvaluatingHallOfFame) return 0;
		
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
		
		// At this point, Player must be a Heuristic Board Game Player, and bgh must be a NNBoardGameHeuristic
		
		if(evaluated.containsKey(genotypeID)){
			return evaluated.get(genotypeID);
		}else if(MMNEAT.ea.currentGeneration() > 0){ // Must complete at least one full Generation
			currentlyEvaluatingHallOfFame = true; // prevent infinite recursion when evaluating hall of fame
			Pair<double[], double[]> evalResults = MMNEAT.hallOfFame.eval(((NNBoardGameHeuristic<?,S>) bgh).getGenotype());
			currentlyEvaluatingHallOfFame = false;
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

	@Override
	public double getMinScore() {
		return -2; // Presumably uses the SimpleWinLoseDraw Fitness Function
	}

}
