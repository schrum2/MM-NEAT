package edu.southwestern.boardGame.fitnessFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.heuristics.BoardGameHeuristic;
import edu.southwestern.boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.evolution.GenerationalEA;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;

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
		fitFunctions.add(new WinPercentageBoardGameFitness<T>());
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
		 int testGen = ((GenerationalEA) MMNEAT.ea).currentGeneration();
		 
		 if(currentGen != testGen){
			evaluated.clear(); 
			currentGen = testGen;
		 }
	}

	@Override
	public double getMinScore() {
		return -2; // Uses the Win Percentage Board Game Fitness Function
	}

}