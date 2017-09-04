package edu.southwestern.experiment.post;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerRandom;
import edu.southwestern.boardGame.agents.HeuristicBoardGamePlayer;
import edu.southwestern.boardGame.checkers.Checkers;
import edu.southwestern.boardGame.featureExtractor.BoardGameFeatureExtractor;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.CheckersAdvancedFitness;
import edu.southwestern.boardGame.fitnessFunction.OthelloPieceFitness;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.fitnessFunction.WinPercentageBoardGameFitness;
import edu.southwestern.boardGame.heuristics.NNBoardGameHeuristic;
import edu.southwestern.boardGame.othello.Othello;
import edu.southwestern.boardGame.othello.OthelloState;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.experiment.Experiment;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.CommonTaskUtil;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.tasks.SinglePopulationTask;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.PopulationUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.graphics.DrawingPanel;

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
					population.add(PopulationUtil.extractGenotype(file));
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
		
		for(Genotype<T> gene: population) {
			
			DrawingPanel panel = null;
			DrawingPanel cppnPanel = null;

			if(CommonConstants.watch){
				Pair<DrawingPanel, DrawingPanel> drawPanels = CommonTaskUtil.getDrawingPanels(gene);

				panel = drawPanels.t1;
				cppnPanel = drawPanels.t2;

				panel.setVisible(true);
				cppnPanel.setVisible(true);
			}


			player.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getId(), featExtract, gene)));
			@SuppressWarnings("unchecked")
			BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};

			// TEMPORARY! For checking how WPC performs against random.
			// Apparently, completely random play will beat the WPC about 16% of the time.
			// Obviously, the random player will have a chance of selecting the best moves,
			// but I find this somewhat shocking. I need to figure out if this result is
			// reasonable, or indicates that there really is a bug in how alpha-beta or minimax
			// works (since this would affect evolved heuristics as well)
			
			//players = new BoardGamePlayer[]{new BoardGamePlayerRandom<OthelloState>() , opponent};

			
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

			System.out.println("Press enter");
			MiscUtil.waitForReadStringAndEnterKeyPress();
			
			if (panel != null) {
				panel.dispose();
			} 
			if(cppnPanel != null) {
				cppnPanel.dispose();
			}
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
