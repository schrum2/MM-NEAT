package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.CheckersAdvancedFitness;
import boardGame.fitnessFunction.HallOfFameFitness;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.heuristics.NNBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.networks.NetworkTask;
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATTask;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class StaticOpponentBoardGameTask<T extends Network, S extends BoardGameState> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask {

	BoardGamePlayer<S> opponent;
	HeuristicBoardGamePlayer<S> player;
	BoardGameFeatureExtractor<S> featExtract;
	
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	List<BoardGameFitnessFunction<S>> otherScores = new ArrayList<BoardGameFitnessFunction<S>>();
	
	/**
	 * Constructor for a new BoardGameTask
	 */
	@SuppressWarnings("unchecked")
	public StaticOpponentBoardGameTask(){
		try {
			opponent = (BoardGamePlayer<S>) ClassCreation.createObject("boardGameOpponent"); // The Opponent
			player = (HeuristicBoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.out.println("BoardGame instance could not be loaded");
			System.exit(1);
		}
		
		// Add Fitness Functions here to add as Selection Functions
		if(Parameters.parameters.booleanParameter("boardGameSimpleFitness")){
			fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		}
		if(Parameters.parameters.booleanParameter("boardGameCheckersFitness")){
			fitFunctions.add(new CheckersAdvancedFitness<S>());
		}
		if(Parameters.parameters.booleanParameter("hallOfFame")){
			fitFunctions.add(new HallOfFameFitness<S>());
		}
		
		for(BoardGameFitnessFunction<S> fit: fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName());
		}
		
		// Add Fitness Functions here to keep track of Other Scores
		otherScores.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		for(BoardGameFitnessFunction<S> fit : otherScores){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
	}

	public int numOtherScores() {
		// Other Scores are kept in the fitFunctions ArrayList;
		// everything except the first Fitness Function is an Other Score
		return otherScores.size();
	}
	
	/**
	 * Returns the number of Objectives for the BoardGameTask
	 * 
	 * @return The number of Objectives for the BoardGameTask
	 */
	@Override
	public int numObjectives() {
		return 1;
	}

	/**
	 * Returns the TimeStamp for a BoardGameTask
	 * 
	 * @return 0, because the TimeStamp doesn't appear useful for this task
	 */
	@Override
	public double getTimeStamp() {
		// Doesn't appear to be necessary for this Task, but may be used later.
		return 0;
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

	/**
	 * Returns the Behavior Vector for Behavioral Diversity
	 */
	@Override
	public ArrayList<Double> getBehaviorVector() {
		return ArrayUtil.doubleVectorFromArray(MMNEAT.boardGame.getDescription());
	}

	/**
	 * Evaluates a given individual network's Fitness;
	 * If the CommonConstants Watch variable is set to "True," runs a visual evaluation,
	 * Else runs a non-visual evaluation
	 * 
	 * @param individual Genotype<T> specifying a Network to be evaluated
	 * @param num Integer value
	 * @return Pair of Double Arrays that show the Fitness of an individual network
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Pair<double[], double[]> oneEval(Genotype<T> individual, int num) {

		player.setHeuristic((new NNBoardGameHeuristic<T,S>(individual.getId(), individual.getPhenotype(), featExtract)));
		BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};
		// get(0) because information for both players is returned, but only the first is about the evolved player
		return BoardGameUtil.playGame((BoardGame<S>) MMNEAT.boardGame, players, fitFunctions, otherScores).get(0);
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
	
}
