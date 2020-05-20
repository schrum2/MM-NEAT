package edu.southwestern.tasks.boardGame;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.OthelloPieceFitness;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.fitnessFunction.WinPercentageBoardGameFitness;
import edu.southwestern.boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.NetworkTask;
import edu.southwestern.networks.hyperneat.HyperNEATTask;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.SubstrateConnectivity;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.GroupTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;

public class MultiPopulationCompetativeCoevolutionBoardGameTask<S extends BoardGameState> extends GroupTask implements NetworkTask, HyperNEATTask  {

	BoardGame<S> bg;
	BoardGamePlayer<S>[] players;
	BoardGameFeatureExtractor<S> featExtract;
	
	List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
	List<BoardGameFitnessFunction<S>> otherScores = new ArrayList<BoardGameFitnessFunction<S>>();
	
	@SuppressWarnings("unchecked")
	public MultiPopulationCompetativeCoevolutionBoardGameTask(){
		
		try {
			bg = (BoardGame<S>) ClassCreation.createObject("boardGame");
			players = new BoardGamePlayer[numberOfPopulations()];
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			for(int i = 0; i < numberOfPopulations(); i++){
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
		if(Parameters.parameters.booleanParameter("boardGameOthelloFitness")){
			fitFunctions.add((BoardGameFitnessFunction<S>) new OthelloPieceFitness());
		}
		
		// Add Fitness Functions here to keep track of Other Scores
		otherScores.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		otherScores.add(new WinPercentageBoardGameFitness<S>());
		
		for(int i = 0; i < bg.getNumPlayers(); i++){
			for(BoardGameFitnessFunction<S> fit : fitFunctions){
				MMNEAT.registerFitnessFunction(fit.getFitnessName(),true,i);
			}
			
			for(BoardGameFitnessFunction<S> fit : otherScores){
				MMNEAT.registerFitnessFunction(fit.getFitnessName(),false,i);
			}
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
		// Returns an Array to store the Other Scores for each Population
		return ArrayUtil.intOnes(otherScores.size());
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
			evolved.setHeuristic((new NNBoardGameHeuristic(gene.getId(), featExtract, gene)));
			teamPlayers[index++] = evolved;
		}
		// End of Copied Code
		
		ArrayList<Pair<double[], double[]>> scored = BoardGameUtil.playGame(bg, teamPlayers, fitFunctions, otherScores);
		
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
		return otherScores.size()-1;
	}
	
	@Override
	public int numCPPNInputs() {
		return HyperNEATTask.DEFAULT_NUM_CPPN_INPUTS;
	}

	@Override
	public double[] filterCPPNInputs(double[] fullInputs) {
		return fullInputs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Substrate> getSubstrateInformation() {
		return BoardGameUtil.getSubstrateInformation(MMNEAT.boardGame);
	}

	@Override
	public List<SubstrateConnectivity> getSubstrateConnectivity() {
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

	@Override
	public void flushSubstrateMemory() {
		// Does nothing: This task does not cache substrate information
	}

}
