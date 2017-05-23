package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;

import boardGame.BoardGame;
import boardGame.BoardGamePlayer;
import boardGame.BoardGamePlayerOneStepEval;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class SinglePopulationCompetativeCoevolutionBoardGameTask<T extends Network> extends SinglePopulationCoevolutionTask<T> {

	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer opponent;
	
	@SuppressWarnings("rawtypes")
	SinglePopulationCompetativeCoevolutionBoardGameTask(){
		MMNEAT.registerFitnessFunction("Win Reward");
		
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			opponent = (BoardGamePlayer) ClassCreation.createObject("boardGameOpponent"); // The opponent
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
	}
	
	@Override
	public int numObjectives() {
		return 1;
	}

	@Override
	public double[] minScores() {
		return new double[]{-1}; // -1 is for a loss
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group) {
		BoardGamePlayer[] players = new BoardGamePlayer[group.size()];
		int index = 0;
		for(Genotype<T> gene : group){
			BoardGamePlayer evolved = new BoardGamePlayerOneStepEval<T>(gene.getPhenotype());
			players[index++] = evolved;
		}
		return BoardGameUtil.playGame(bg, players);
	}

	@Override
	public int groupSize() {
		return bg.getNumPlayers();
	}


}
