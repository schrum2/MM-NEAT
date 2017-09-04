package boardGame.fitnessFunction;

import java.util.HashMap;
import java.util.Map;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.heuristics.BoardGameHeuristic;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.Network;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;

public class HallOfFameFitness<T extends Network, S extends BoardGameState> implements BoardGameFitnessFunction<S> {
	// This constant is problematic. It assumes the Hall of Fame fitness will always be index 0,
	// and that there will only be one selection function in index 1.
	public static final int SELECTION_INDEX = 1; 
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
		if(currentlyEvaluatingHallOfFame) {
			//System.out.println("Evaluating Hall of Fame");
			return 0;
		}
		
		long genotypeID = -1;
		BoardGameHeuristic<S> bgh = null;
		
		if(player instanceof HeuristicBoardGamePlayer){
			bgh = ((HeuristicBoardGamePlayer<S>) player).getHeuristic();
			if(bgh instanceof NNBoardGameHeuristic){
				genotypeID = ((NNBoardGameHeuristic<?,S>) bgh).getID();
			} else {
				System.out.println("Not a NNBoardGameHeuristic");
				System.out.println("Don't have static opponents play against other static opponents");
				System.out.println(player);
				System.out.println(bgh);
				System.exit(1);
			}
		} else {
			System.out.println("Not a HeuristicBoardGamePlayer");
			System.out.println("Don't have static opponents play against other static opponents");
			System.out.println(player);
			System.exit(1);
		}
		
		// At this point, Player must be a Heuristic Board Game Player, and bgh must be a NNBoardGameHeuristic
		
		if(evaluated.containsKey(genotypeID)){
			return evaluated.get(genotypeID);
		}else if(MMNEAT.ea.currentGeneration() > 0){ // Must complete at least one full Generation
			currentlyEvaluatingHallOfFame = true; // prevent infinite recursion when evaluating hall of fame
			Pair<double[], double[]> evalResults = MMNEAT.hallOfFame.eval(((NNBoardGameHeuristic<?,S>) bgh).getGenotype());
			currentlyEvaluatingHallOfFame = false;
			double score = evalResults.t1[SELECTION_INDEX]; // Only uses 1 Selection Function
			
			evaluated.put(genotypeID, score);
			
			return score;
		}else{
			//System.out.println("Generation 0: "+MMNEAT.ea.currentGeneration());
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
