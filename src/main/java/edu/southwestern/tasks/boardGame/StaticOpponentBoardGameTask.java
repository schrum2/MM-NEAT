package edu.southwestern.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.OpeningRandomMovesScore;
import edu.southwestern.boardGame.fitnessFunction.OthelloPieceFitness;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.fitnessFunction.WinPercentageBoardGameFitness;
import edu.southwestern.boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mulambda.MuLambda;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class StaticOpponentBoardGameTask<T extends Network, S extends BoardGameState> extends NoisyLonerTask<T> implements NetworkTask, HyperNEATTask {

	private int winRateIndex = 0;
	private boolean increaseRandomMoves = false;
	
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
		if(Parameters.parameters.booleanParameter("boardGameOthelloFitness")){
			fitFunctions.add((BoardGameFitnessFunction<S>) new OthelloPieceFitness());
		}
		if(Parameters.parameters.booleanParameter("boardGameWinPercentFitness")) {
			fitFunctions.add(new WinPercentageBoardGameFitness<S>());
		}
		
		for(BoardGameFitnessFunction<S> fit: fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName());
		}
		
		int index = 0; // index in other scores
		// Add Fitness Functions here to keep track of Other Scores
		otherScores.add(new SimpleWinLoseDrawBoardGameFitness<S>()); 
		index++;
		winRateIndex = index; // tracked to see if number of random opens needs to increase
		otherScores.add(new WinPercentageBoardGameFitness<S>());
		index++;
		otherScores.add(new OpeningRandomMovesScore<S>());
		index++;
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
		return fitFunctions.size();
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

		player.setHeuristic((new NNBoardGameHeuristic<T,S>(individual.getId(), featExtract, individual)));
		BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};
		// get(0) because information for both players is returned, but only the first is about the evolved player
		return BoardGameUtil.playGame((BoardGame<S>) MMNEAT.boardGame, players, fitFunctions, otherScores).get(0);
	}
	
	/**
	 * Increase random starting moves only if all matches across all trials were won
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Score<T> evaluate(Genotype<T> individual) {
		Score<T> result = super.evaluate(individual);
		// Assume a Mu Lambda EA will be used, which may not always be true. 
		// Only allows updating during parent evaluations.
		if(Parameters.parameters.booleanParameter("boardGameIncreasingRandomOpens") && ((MuLambda<T>) MMNEAT.ea).evaluatingParents) {
			if(result.otherStats[winRateIndex] == 1.0) {
				increaseRandomMoves = true;
			}
		}
		return result;
	}

	/**
	 * Check if boardGameOpeningRandomMoves needs to increase
	 */
	@Override
	public ArrayList<Score<T>> evaluateAll(ArrayList<Genotype<T>> population) {
		increaseRandomMoves = false;
		ArrayList<Score<T>> result = super.evaluateAll(population);
		// increaseRandomMoves might be true from a oneEval call
		if(increaseRandomMoves) {
			Parameters.parameters.setInteger("boardGameOpeningRandomMoves", Parameters.parameters.integerParameter("boardGameOpeningRandomMoves") + 1);
			System.out.println("More opening random moves: " + Parameters.parameters.integerParameter("boardGameOpeningRandomMoves"));
		}
		increaseRandomMoves = false;
		return result;
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
	
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
		return BoardGameUtil.getSubstrateConnectivity();
	}
	
}
