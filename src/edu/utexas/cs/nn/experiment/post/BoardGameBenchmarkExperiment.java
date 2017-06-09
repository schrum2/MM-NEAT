package edu.utexas.cs.nn.experiment.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.HeuristicBoardGamePlayer;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.TWEANNGenotype;
import edu.utexas.cs.nn.evolution.nsga2.NSGA2Score;
import edu.utexas.cs.nn.experiment.Experiment;
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

public class BoardGameBenchmarkExperiment<T> implements Experiment{
	
	protected ArrayList<Genotype<T>> population;
	protected SinglePopulationTask<T> task;

	
	private BoardGame bg;
	private BoardGameFitnessFunction selectionFunction;
	private BoardGameFeatureExtractor featExtract;
	private BoardGamePlayer[] players;
	private BoardGamePlayer opponent;
	
	private List<BoardGameFitnessFunction> fitFunctions = new ArrayList<BoardGameFitnessFunction>();
	
	
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
			System.out.println("Loading: " + lastSavedDir);
			population = PopulationUtil.load(lastSavedDir);
//			if (Parameters.parameters.booleanParameter("onlyWatchPareto")) { // Always only evaluates the Pareto Front
				NSGA2Score<T>[] scores = null;
				try {
					scores = PopulationUtil.loadScores(Parameters.parameters.integerParameter("lastSavedGeneration"));
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
				PopulationUtil.pruneDownToParetoFront(population, scores);
//			}
		}
		// End section from LoadAndWatchExperiment
		
		
		// TODO: Find a way to load up these specifications from the directory?/Parameters File?
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
			selectionFunction = (BoardGameFitnessFunction<?>) ClassCreation.createObject("boardGameFitnessFunction");
			featExtract = (BoardGameFeatureExtractor<BoardGameState>) ClassCreation.createObject("boardGameFeatureExtractor");
			opponent = (BoardGamePlayer) ClassCreation.createObject("boardGameOpponent");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

		MMNEAT.registerFitnessFunction(selectionFunction.getFitnessName());
		
		// Add Other Scores here to keep track of other Fitness Functions
		fitFunctions.add(new SimpleWinLoseDrawBoardGameFitness());
		
		for(BoardGameFitnessFunction fit : fitFunctions){
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
			
			try {
				HeuristicBoardGamePlayer evolved = (HeuristicBoardGamePlayer) ClassCreation.createObject("boardGamePlayer");
				
				// TODO: Create BoardGamePlayer "evolved" Heuristic with "gene"
				
				players = new BoardGamePlayer[]{evolved, opponent};
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			
			
			BoardGameUtil.playGame(bg, players, fitFunctions);
			
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
