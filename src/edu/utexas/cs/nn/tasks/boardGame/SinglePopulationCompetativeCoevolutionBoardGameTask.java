package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.CheckersAdvancedFitness;
import boardGame.fitnessFunction.HallOfFameFitness;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.fitnessFunction.StaticOtherOpponentFitness;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.SinglePopulationCoevolutionTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class SinglePopulationCompetativeCoevolutionBoardGameTask<T extends Network, S extends BoardGameState> extends SinglePopulationCoevolutionTask<T> implements NetworkTask, HyperNEATTask  {

	BoardGamePlayer<S>[] players;
	BoardGameFeatureExtractor<S> featExtract;
	
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	List<BoardGameFitnessFunction<S>> otherScores = new ArrayList<BoardGameFitnessFunction<S>>();
	
	@SuppressWarnings("unchecked")
	public SinglePopulationCompetativeCoevolutionBoardGameTask(){
		try {
			players = new BoardGamePlayer[groupSize()];
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			for(int i = 0; i < groupSize(); i++){
				players[i] = (BoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		
		// Add Fitness Functions here to act as Selection Functions
		if(Parameters.parameters.booleanParameter("boardGameSimpleFitness")){
			fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		}
		if(Parameters.parameters.booleanParameter("boardGameCheckersFitness")){
			fitFunctions.add(new CheckersAdvancedFitness<S>());
		}
		if(Parameters.parameters.booleanParameter("hallOfFame")){
			fitFunctions.add(new HallOfFameFitness<T,S>());
		}
		
		for(BoardGameFitnessFunction<S> fit : fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName());
		}
		
		// Add Fitness Functions here to keep track of Other Scores
		otherScores.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		otherScores.add(new StaticOtherOpponentFitness<S>()); // Automatically is set to boardGameOpponent
		
		for(BoardGameFitnessFunction<S> fit : otherScores){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
	}
	
	@Override
	public int numObjectives() {
		return 1;
	}

	public int numOtherScores() {
		// Other Scores are kept in the fitFunctions ArrayList;
		// everything except the first Fitness Function is an Other Score
		return otherScores.size();
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
			evolved.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getId(), gene.getPhenotype(),featExtract, gene)));
			teamPlayers[index++] = evolved;
		}
		return BoardGameUtil.playGame(MMNEAT.boardGame, teamPlayers, fitFunctions, otherScores);
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
