package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.heuristics.BoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;
import edu.utexas.cs.nn.util.ClassCreation;

public class MultiPopulationCompetativeCoevolutionBoardGameTask extends GroupTask{

	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer player;
	@SuppressWarnings("rawtypes")
	BoardGameHeuristic playerHeuristic;	
	
	@SuppressWarnings("rawtypes")
	public MultiPopulationCompetativeCoevolutionBoardGameTask(){
		MMNEAT.registerFitnessFunction("Win Reward");
		
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			player = (BoardGamePlayer) ClassCreation.createObject("boardGamePlayer"); // The Player
			playerHeuristic = (BoardGameHeuristic) ClassCreation.createObject("boardGamePlayerHeuristic"); // The Player's Heuristic
			player.setHeuristic(playerHeuristic); // Set's the Heuristic for the Opponent
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
	}
	
	@Override
	public int numberOfPopulations() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] objectivesPerPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] otherStatsPerPopulation() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		
		BoardGameUtil.playGame(bg, null); // TODO
		
		return null;
	}

}
