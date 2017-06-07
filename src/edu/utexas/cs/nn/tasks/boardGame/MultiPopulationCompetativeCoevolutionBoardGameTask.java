package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;

import boardGame.BoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

public class MultiPopulationCompetativeCoevolutionBoardGameTask extends GroupTask{

	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer[] players;
	@SuppressWarnings("rawtypes")
	BoardGameFeatureExtractor featExtract;
	@SuppressWarnings("rawtypes")
	BoardGameFitnessFunction fitnessFunction;
	
	@SuppressWarnings("rawtypes")
	public MultiPopulationCompetativeCoevolutionBoardGameTask(){
		MMNEAT.registerFitnessFunction("Win Reward");
		
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			players = new BoardGamePlayer[numberOfPopulations()];
			featExtract = (BoardGameFeatureExtractor) ClassCreation.createObject("boardGameFeatureExtractor");
			fitnessFunction = (BoardGameFitnessFunction) ClassCreation.createObject("boardGameFitnessFunction");
			for(int i = 0; i < numberOfPopulations(); i++){
				players[i] = (BoardGamePlayer) ClassCreation.createObject("boardGamePlayer"); // The Player
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
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

	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<Score> evaluate(Genotype[] team) {
		// TODO
		BoardGameUtil.playGame(bg, players, fitnessFunction);
		// TODO
		return null;
	}

}
