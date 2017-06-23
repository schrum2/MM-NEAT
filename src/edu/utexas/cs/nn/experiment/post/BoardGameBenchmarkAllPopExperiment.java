package edu.utexas.cs.nn.experiment.post;

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
import edu.utexas.cs.nn.experiment.Experiment;
import edu.utexas.cs.nn.networks.Network;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.CommonTaskUtil;
import edu.utexas.cs.nn.tasks.SinglePopulationTask;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.PopulationUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

public class BoardGameBenchmarkAllPopExperiment<T extends Network, S extends BoardGameState> implements Experiment{
	
	protected ArrayList<Genotype<T>> population;
	protected SinglePopulationTask<T> task;

	
	private BoardGame<S> bg;
	private BoardGameFitnessFunction<S> selectionFunction;
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
			System.out.println("Loading: " + lastSavedDir);
			population = PopulationUtil.load(lastSavedDir);
			}
		
		try {
			bg = (BoardGame<S>) ClassCreation.createObject("boardGame");
			selectionFunction = (BoardGameFitnessFunction<S>) ClassCreation.createObject("boardGameFitnessFunction");
			featExtract = (BoardGameFeatureExtractor<S>) ClassCreation.createObject("boardGameFeatureExtractor");
			player = (HeuristicBoardGamePlayer<S>) ClassCreation.createObject("boardGamePlayer"); // The Player
			opponent = (BoardGamePlayer<S>) ClassCreation.createObject("boardGameOpponent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

		MMNEAT.registerFitnessFunction(selectionFunction.getFitnessName());
		
		// Add Other Scores here to keep track of other Fitness Functions
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness<S>());
		
		for(BoardGameFitnessFunction<S> fit : fitFunctions){
			MMNEAT.registerFitnessFunction(fit.getFitnessName(), false);
		}
		
		fitFunctions.add(0, selectionFunction);
		
	}
	
	/**
	 * Pits the best Co-Evolved BoardGamePlayer against a Static Opponent
	 */
	@Override
	public void run() {
		
		for(Genotype<T> gene : population){
			
			DrawingPanel panel = null;
			DrawingPanel cppnPanel = null;
			
			if(CommonConstants.watch){
				Pair<DrawingPanel, DrawingPanel> drawPanels = CommonTaskUtil.getDrawingPanels(gene);
				
				panel = drawPanels.t1;
				cppnPanel = drawPanels.t2;
				
				panel.setVisible(true);
				cppnPanel.setVisible(true);
			}
			
			
			player.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getId(), gene.getPhenotype(), featExtract)));
			@SuppressWarnings("unchecked")
			BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};

			for(int i = 0; i < CommonConstants.trials; i++){
				BoardGameUtil.playGame(bg, players, fitFunctions, new ArrayList<>()); // No Other Scores
			}
			
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
