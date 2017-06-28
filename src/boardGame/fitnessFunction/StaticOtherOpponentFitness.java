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
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class StaticOtherOpponentFitness<T extends BoardGameState> implements BoardGameFitnessFunction<T> {
	
	BoardGamePlayer<T> opponent;
	int currentGen = -1;
	int matches;
	List<BoardGameFitnessFunction<T>> fitFunctions = new ArrayList<BoardGameFitnessFunction<T>>();
	
	Map<Long, Double> evaluated = new HashMap<Long, Double>();

	@SuppressWarnings("unchecked")
	public StaticOtherOpponentFitness(){
		try {
			opponent = (BoardGamePlayer<T>) ClassCreation.createObject("boardGameOpponent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		matches = Parameters.parameters.integerParameter("boardGameStaticOpponentRuns");
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<T>());
	}
	
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
			BoardGamePlayer<T>[] players = new BoardGamePlayer[]{player, opponent};
			
			double[][] fitness = new double[matches][];
			double[][] other = new double[matches][];
			
			for(int i = 0; i < matches; i++){
				ArrayList<Pair<double[], double[]>> game = BoardGameUtil.playGame(MMNEAT.boardGame, players, fitFunctions, new ArrayList<BoardGameFitnessFunction<T>>()); // No Other Scores
				fitness[i] = game.get(0).t1;
				other[i] = game.get(0).t2;
			}
			
			Pair<double[], double[]> score = NoisyLonerTask.averageResults(fitness, other);
			evaluated.put(genotypeID, score.t1[0]); // Only uses one Fitness Function right now
			
			return score.t1[0];
		}
	}

	@Override
	public void updateFitness(BoardGameState bgs, int index) {
		// Doesn't update until getFitness
	}

	@Override
	public String getFitnessName() {
		return "Static Opponent Fitness";
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
		return -2; // Uses the SimpleWinLoseDraw Fitness Function
	}

}