package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class SinglePopulationCompetativeCoevolutionBoardGameTask<T extends Network, S extends BoardGameState> extends SinglePopulationCoevolutionTask<T> implements NetworkTask, HyperNEATTask  {

	BoardGamePlayer<S>[] players;
	BoardGameFeatureExtractor<S> featExtract;
	BoardGameFitnessFunction<S> selectionFunction;
	
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	
	@SuppressWarnings("unchecked")
	public SinglePopulationCompetativeCoevolutionBoardGameTask(){
		try {
			players = new BoardGamePlayer[groupSize()];
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			selectionFunction = (BoardGameFitnessFunction<S>) ClassCreation.createObject("boardGameFitnessFunction");
			for(int i = 0; i < groupSize(); i++){
				players[i] = (BoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		
		MMNEAT.registerFitnessFunction(selectionFunction.getFitnessName());
		
		// Add Other Scores here to keep track of other Fitness Functions
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		
		for(BoardGameFitnessFunction<S> fit : fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
		fitFunctions.add(0, selectionFunction); // Adds the Selection Function to the first Index
	}
	
	@Override
	public int numObjectives() {
		return 1;
	}

	@Override
	public double[] minScores() {
		return new double[]{-1}; // -1 is for a loss
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group) {
		HeuristicBoardGamePlayer<S>[] teamPlayers = new HeuristicBoardGamePlayer[group.size()];
		int index = 0;
		for(Genotype<T> gene : group){
			HeuristicBoardGamePlayer<S> evolved = (HeuristicBoardGamePlayer<S>) players[index]; // Creates the Player based on the command line
			evolved.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getPhenotype(),featExtract)));
			teamPlayers[index++] = evolved;
		}
		return BoardGameUtil.playGame(MMNEAT.boardGame, teamPlayers, fitFunctions);
	}

	@Override
	public int groupSize() {
		return MMNEAT.boardGame.getNumPlayers();
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

	@SuppressWarnings("unchecked")
	public List<Substrate> getSubstrateInformation() {
		return BoardGameUtil.getSubstrateInformation(MMNEAT.boardGame);
	}
	
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		return BoardGameUtil.getSubstrateConnectivity();
	}
	
	/**
	 * Returns a String containing the Sensor Labels for the BoardGameTask
	 * 
	 * @return String containing the Sensor Labels for the BoardGameTask
	 */
	@Override
	public String[] sensorLabels() {
		return featExtract.getFeatureLabels();
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

	@Override
	public void preEval() {
		// No action required
	}


}
