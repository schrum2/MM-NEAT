package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
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
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.tasks.GroupTask;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class MultiPopulationCompetativeCoevolutionBoardGameTask<S extends BoardGameState> extends GroupTask implements NetworkTask, HyperNEATTask  {

	BoardGame<S> bg;
	BoardGamePlayer<S>[] players;
	BoardGameFeatureExtractor<S> featExtract;
	BoardGameFitnessFunction<S> selectionFunction;
	
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	
	@SuppressWarnings("unchecked")
	public MultiPopulationCompetativeCoevolutionBoardGameTask(){
		
		try {
			bg = (BoardGame<S>) ClassCreation.createObject("boardGame");
			players = new BoardGamePlayer[numberOfPopulations()];
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			selectionFunction = (BoardGameFitnessFunction<S>) ClassCreation.createObject("boardGameFitnessFunction");
			for(int i = 0; i < numberOfPopulations(); i++){
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
		// Returns an Array to store the Other Scores for each Population
		return new int[(fitFunctions.size()-1)];
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
		HeuristicBoardGamePlayer<S>[] teamPlayers = new HeuristicBoardGamePlayer[team.length];
		int index = 0;
		for(Genotype gene : team){
			HeuristicBoardGamePlayer<S> evolved = (HeuristicBoardGamePlayer<S>) players[index]; // Creates the Player based on the command line
			evolved.setHeuristic((new NNBoardGameHeuristic(gene.getId(), (Network) gene.getPhenotype(), featExtract)));
			teamPlayers[index++] = evolved;
		}
		// End of Copied Code
		
		ArrayList<Pair<double[], double[]>> scored = BoardGameUtil.playGame(bg, teamPlayers, fitFunctions);
		
		ArrayList<Score> finalScores = new ArrayList<Score>();
		
		for(int i = 0; i < team.length; i++){
			// Replace null with the behavior representation for this task
			finalScores.add(new Score(team[i], scored.get(i).t1, null, scored.get(i).t2));
		}
		
		return finalScores;
	}

	public int numOtherScores() {
		// Other Scores are kept in the fitFunctions ArrayList;
		// everything except the first Fitness Function is an Other Score
		return fitFunctions.size()-1;
	}
	
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}

	@Override
	public List<Substrate> getSubstrateInformation() {
		return BoardGameUtil.getSubstrateInformation(MMNEAT.boardGame);
	}

	@Override
	public List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		return BoardGameUtil.getSubstrateConnectivity();
	}

	@Override
	public String[] sensorLabels() {
		return featExtract.getFeatureLabels();
	}

	@Override
	public String[] outputLabels() {
		return new String[]{"Utility"};
	}

}
