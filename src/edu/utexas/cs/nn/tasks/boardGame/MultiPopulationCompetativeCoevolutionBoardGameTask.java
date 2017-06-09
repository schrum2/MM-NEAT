package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class MultiPopulationCompetativeCoevolutionBoardGameTask extends GroupTask{

	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer[] players;
	@SuppressWarnings("rawtypes")
	BoardGameFeatureExtractor featExtract;
	@SuppressWarnings("rawtypes")
	BoardGameFitnessFunction selectionFunction;
	
	List<BoardGameFitnessFunction> fitFunctions = new ArrayList<BoardGameFitnessFunction>();

	
	@SuppressWarnings("rawtypes")
	public MultiPopulationCompetativeCoevolutionBoardGameTask(){
		
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			players = new BoardGamePlayer[numberOfPopulations()];
			featExtract = (BoardGameFeatureExtractor) ClassCreation.createObject("boardGameFeatureExtractor");
			selectionFunction = (BoardGameFitnessFunction) ClassCreation.createObject("boardGameFitnessFunction");
			for(int i = 0; i < numberOfPopulations(); i++){
				players[i] = (BoardGamePlayer) ClassCreation.createObject("boardGamePlayer"); // The Player
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		
		MMNEAT.registerFitnessFunction(selectionFunction.getFitnessName());
		
		// Add Other Scores here to keep track of other Fitness Functions
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness());
		
		for(BoardGameFitnessFunction fit : fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
		fitFunctions.add(0, selectionFunction); // Adds the Selection Function to the first Index
	}
	
	@Override
	public int numberOfPopulations() {
		return bg.getNumPlayers();
	}

	@Override
	public int[] objectivesPerPopulation() {
		// There's only 1 Objective per Population
		return ArrayUtil.intOnes(numberOfPopulations());
	}

	@Override
	public int[] otherStatsPerPopulation() {
		// There are no other Stats for the Populations
		return new int[numberOfPopulations()];
	}

	@Override
	public double getTimeStamp() {
		// Many Domains don't use TimeStamp
		return 0;
	}

	@Override
	public void finalCleanup() {
		// Default to empty
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<Score> evaluate(Genotype[] team) {
		
		// Copied from SinglePopulationCompetativeCoevolutionBoardGameTask
		HeuristicBoardGamePlayer[] teamPlayers = new HeuristicBoardGamePlayer[team.length];
		int index = 0;
		for(Genotype gene : team){
			HeuristicBoardGamePlayer evolved = (HeuristicBoardGamePlayer) players[index]; // Creates the Player based on the command line
			evolved.setHeuristic((new NNBoardGameHeuristic((Network) gene.getPhenotype(), featExtract)));
			teamPlayers[index++] = evolved;
		}
		
		BoardGamePlayer[] play = new BoardGamePlayer[]{};
		// End of Copied Code
		
		ArrayList<Pair<double[], double[]>> scored = BoardGameUtil.playGame(bg, players, fitFunctions);
		
		ArrayList<Score> finalScores = new ArrayList<Score>();
		
		// TODO: Unsure on if this part works; everything above was copied, but this returns something different. Double-check the Score?
		for(int i = 0; i < team.length; i++){
			finalScores.add(new Score(team[i], scored.get(i).t1, scored));
		}
		
		return finalScores;
	}

}
