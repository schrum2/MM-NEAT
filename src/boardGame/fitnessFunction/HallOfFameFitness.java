package boardGame.fitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.heuristics.BoardGameHeuristic;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class HallOfFameFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T> {
	
	BoardGameFitnessFunction<T> selectionFunction;
	int currentGen = -1;
	List<BoardGameFitnessFunction<T>> fitFunctions = new ArrayList<BoardGameFitnessFunction<T>>();
	
	Map<Long, Double> evaluated = new HashMap<Long, Double>();
	
	@SuppressWarnings("unchecked")
	@Override
	public double getFitness(BoardGamePlayer<T> player, int index) {
		long genotypeID = -1;
		BoardGameHeuristic<T> bgh;
		
		if(player instanceof HeuristicBoardGamePlayer){
			bgh = ((HeuristicBoardGamePlayer<T>) player).getHeuristic();
			if(bgh instanceof NNBoardGameHeuristic){
				genotypeID = ((NNBoardGameHeuristic<?,T>) bgh).getID();
			} else {
				return 0; // Don't have static opponents play against other static opponents
			}
		} else {
			return 0; // Don't have static opponents play against other static opponents
		}
		
		if(evaluated.containsKey(genotypeID)){
			return evaluated.get(genotypeID);
		}else{
			
			List<BoardGamePlayer<T>> champs = null; // TODO: Decide how to get Hall Of Fame Champs here
			double[][] scores = new double[champs.size()][fitFunctions.size()]; // [ChampEval][FitFunction]
			
			for(int i = 0; i < champs.size(); i++){
				BoardGamePlayer<T>[] players = new BoardGamePlayer[]{player, champs.get(i)};

				ArrayList<Pair<double[], double[]>> game = BoardGameUtil.playGame(MMNEAT.boardGame, players, fitFunctions, new ArrayList<>()); // No Other Scores
				scores[i] = game.get(0).t1;
			}
			
			Pair<double[], double[]> evalResults = NoisyLonerTask.averageResults(scores, null);
			double score = evalResults.t1[0]; // Only uses 1 Selection Function
			
			evaluated.put(genotypeID, score);
			
			return score;
		}
	}

	@Override
	public void updateFitness(T bgs, int index) {
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
