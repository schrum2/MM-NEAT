package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGameHeuristic;
import boardGame.BoardGamePlayer;
import boardGame.BoardGamePlayerOneStepEval;
import boardGame.BoardGameState;
import boardGame.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;

public class SinglePopulationCompetativeCoevolutionBoardGameTask<T extends Network> extends SinglePopulationCoevolutionTask<T> implements NetworkTask, HyperNEATTask  {

	@SuppressWarnings("rawtypes")
	BoardGame bg;
	@SuppressWarnings("rawtypes")
	BoardGamePlayer opponent;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SinglePopulationCompetativeCoevolutionBoardGameTask(){
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group) {
		BoardGamePlayer[] players = new BoardGamePlayer[group.size()];
		int index = 0;
		for(Genotype<T> gene : group){
			
			BoardGamePlayer evolved = new BoardGamePlayerOneStepEval(new NNBoardGameHeuristic(gene.getPhenotype()));
			players[index++] = evolved;
		}
		return BoardGameUtil.playGame(bg, players);
	}

	@Override
	public int groupSize() {
		return bg.getNumPlayers();
	}

	// Used for Hyper-NEAT
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	// Used for Hyper-NEAT
	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs; // default behavior
	}

	// TODO: Get from 2D board game task once it is moved there from StaticOpponentBoardGameTask
	@Override
	public List<Substrate> getSubstrateInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	// TODO: Get from 2D board game task once it is moved there from StaticOpponentBoardGameTask
	@Override
	public List<Pair<String, String>> getSubstrateConnectivity() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns a String containing the Sensor Labels for the BoardGameTask
	 * 
	 * @return String containing the Sensor Labels for the BoardGameTask
	 */
	@Override
	public String[] sensorLabels() {
		return bg.getFeatureLabels();
	}

	/**
	 * Returns a String containing the Output Labels for the BoardGameTask
	 * 
	 * @return String containing the Output Labels for the BoardGameTask
	 */
	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}


}
