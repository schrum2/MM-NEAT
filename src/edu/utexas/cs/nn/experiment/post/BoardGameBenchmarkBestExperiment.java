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
import edu.utexas.cs.nn.util.file.FileUtilities;
import edu.utexas.cs.nn.util.graphics.DrawingPanel;

public class BoardGameBenchmarkBestExperiment<T extends Network, S extends BoardGameState> implements Experiment{
	
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
		
		/*
		 * Copied from: LoadAndWatchExperiment
		 */
		String lastSavedDir = Parameters.parameters.stringParameter("lastSavedDirectory");
		// Currently does not work with co-evolution. Other experiments handle these cases
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
		
		// End section from LoadAndWatchExperiment
		
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
			
			
		player.setHeuristic((new NNBoardGameHeuristic<T,S>(gene.getPhenotype(), featExtract)));
		BoardGamePlayer<S>[] players = new BoardGamePlayer[]{player, opponent};
			
		for(int i = 0; i < CommonConstants.trials; i++){
			BoardGameUtil.playGame(bg, players, fitFunctions);
		}
			
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
