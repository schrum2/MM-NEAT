package edu.utexas.cs.nn.experiment.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.checkers.Checkers;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.CheckersAdvancedFitness;
import boardGame.fitnessFunction.OthelloPieceFitness;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.fitnessFunction.WinPercentageBoardGameFitness;
import boardGame.heuristics.NNBoardGameHeuristic;
import boardGame.othello.Othello;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.experiment.Experiment;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.CommonTaskUtil;
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

/**
 * The benefit that this experiment has over ObjectiveBestNetworksExperiment is that it works with
 * results from coevolution as well.
 *
 * @param <T> phenotype that is a neural network
 * @param <S> type of board game state for the board game
 */
public class BoardGameBenchmarkBestExperiment<T extends Network, S extends BoardGameState> implements Experiment{
	
	protected ArrayList<Genotype<T>> population;
	protected SinglePopulationTask<T> task;

	
	private BoardGame<S> bg;
	private BoardGameFeatureExtractor<S> featExtract;
	private HeuristicBoardGamePlayer<S> player;
	private BoardGamePlayer<S> opponent;
	
	private List<BoardGameFitnessFunction<S>> fitFunctions = new ArrayList<BoardGameFitnessFunction<S>>();
		
	/**
	 * Gets the best Coevolved BoardGamePlayer in a given Task; initializes the boardGame and fitnessFunction
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void init() {

		String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
		
		this.task = (SinglePopulationTask<T>) MMNEAT.task;
		if (lastSavedDir == null || lastSavedDir.equals("")) {
			System.out.println("Nothing to load");
			System.exit(1);
		} else {
			if (Parameters.parameters.booleanParameter("watchLastBest")) {
				population = new ArrayList<Genotype<T>>();
				for(int i = 0; i < MMNEAT.task.numObjectives(); i++) {
					int lastGen = Parameters.parameters.integerParameter("lastSavedGeneration");
					String file = FileUtilities.getSaveDirectory() + "/bestObjectives/gen" + lastGen + "_bestIn"+i+".xml";
					population.add((Genotype<T>) PopulationUtil.extractGenotype(file));
				}
			} else {
				String dir = FileUtilities.getSaveDirectory() + "/bestObjectives";
				population = PopulationUtil.load(dir);
			}
		}
		
		try {
			bg = (BoardGame<S>) ClassCreation.createObject("boardGame");
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			player = (HeuristicBoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			opponent = (BoardGamePlayer<S>) ClassCreation.createObject("boardGameOpponent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Add fitness measures
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		fitFunctions.add(new WinPercentageBoardGameFitness<S>());
		if(bg instanceof Checkers){
			fitFunctions.add(new CheckersAdvancedFitness<S>());
		}
		if(bg instanceof Othello){
			fitFunctions.add((BoardGameFitnessFunction<S>) new OthelloPieceFitness());
		}

		for(BoardGameFitnessFunction<S> ff : fitFunctions) {
			MMNEAT.registerFitnessFunction(ff.getFitnessName());
		}		
	}
	
	/**
	 * Pits the best Static Evolved BoardGamePlayer against a Static Opponent
	 */
	@Override
	public void run() {
		
		Genotype<T> gene = population.get(0);
			
		DrawingPanel panel = null;
		DrawingPanel cppnPanel = null;
			
		if(CommonConstants.watch){
			Pair<DrawingPanel, DrawingPanel> drawPanels = CommonTaskUtil.getDrawingPanels(gene);
				
			panel = drawPanels.t1;
			cppnPanel = drawPanels.t2;
				
			panel.setVisible(true);
			cppnPanel.setVisible(true);
		}
			
			
		player.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getId(), gene.getPhenotype(), featExtract, gene)));
		@SuppressWarnings("unchecked")
		BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};
			
		ArrayList<Pair<double[], double[]>> allResults = new ArrayList<Pair<double[], double[]>>();
		for(int i = 0; i < CommonConstants.trials; i++){
			ArrayList<Pair<double[], double[]>> scores = BoardGameUtil.playGame(bg, players, fitFunctions, new ArrayList<BoardGameFitnessFunction<S>>()); // No Other Scores
			System.out.println(Arrays.toString(scores.get(0).t1)+Arrays.toString(scores.get(0).t2));
			allResults.add(scores.get(0));
		}
		
		double[][] fitness = new double[allResults.size()][];
		double[][] other = new double[allResults.size()][];
		
		for(int i = 0; i < allResults.size(); i++){
			fitness[i] = allResults.get(i).t1;
			other[i] = allResults.get(i).t2;
		}
		
		Pair<double[], double[]> score = NoisyLonerTask.averageResults(fitness, other);
		System.out.println("Average");
		System.out.println(Arrays.toString(score.t1)+Arrays.toString(score.t2));
			
		if (panel != null) {
			panel.dispose();
		} 
		if(cppnPanel != null) {
			cppnPanel.dispose();
		}
	}

	/**
	 * Isn't called; the run() method terminates on its own
	 */
	@Override
	public boolean shouldStop() {
		return true;
	}

}
