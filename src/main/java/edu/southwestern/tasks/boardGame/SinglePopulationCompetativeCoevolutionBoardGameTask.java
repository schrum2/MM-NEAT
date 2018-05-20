package edu.southwestern.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.HallOfFameFitness;
import edu.southwestern.boardGame.fitnessFunction.OthelloPieceFitness;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.fitnessFunction.StaticOtherOpponentFitness;
import edu.southwestern.boardGame.fitnessFunction.WinPercentageBoardGameFitness;
import edu.southwestern.boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.SinglePopulationCoevolutionTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

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
		if(Parameters.parameters.booleanParameter("hallOfFame")){
			fitFunctions.add(new HallOfFameFitness<T,S>());
		}
		if(Parameters.parameters.booleanParameter("boardGameOthelloFitness")){
			fitFunctions.add((BoardGameFitnessFunction<S>) new OthelloPieceFitness());
		}
		
		for(BoardGameFitnessFunction<S> fit : fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName());
		}
		
		// Add Fitness Functions here to keep track of Other Scores
		otherScores.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		otherScores.add(new StaticOtherOpponentFitness<S>()); // Automatically is set to boardGameOpponent
		otherScores.add(new WinPercentageBoardGameFitness<S>());
		
		for(BoardGameFitnessFunction<S> fit : otherScores){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
	}
	
	@Override
	public int numObjectives() {
		return fitFunctions.size();
	}

	public int numOtherScores() {
		// Other Scores are kept in the fitFunctions ArrayList;
		// everything except the first Fitness Function is an Other Score
		return otherScores.size();
	}
	
	@Override
	public double[] minScores() {
		double[] minScore = new double[fitFunctions.size()]; // Get minimum scores from Selection Functions
		int index = 0;
		
		for(BoardGameFitnessFunction<S> fit : fitFunctions){
			minScore[index++] = fit.getMinScore();
		}
		
		return minScore;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Pair<double[], double[]>> evaluateGroup(ArrayList<Genotype<T>> group) {
		HeuristicBoardGamePlayer<S>[] teamPlayers = new HeuristicBoardGamePlayer[group.size()];
		int index = 0;
		for(Genotype<T> gene : group){
			HeuristicBoardGamePlayer<S> evolved = (HeuristicBoardGamePlayer<S>) players[index]; // Creates the Player based on the command line
			evolved.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getId(), featExtract, gene)));
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
