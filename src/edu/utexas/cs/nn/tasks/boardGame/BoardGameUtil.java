package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.TwoDimensionalBoardGame;
import boardGame.TwoDimensionalBoardGameState;
import boardGame.TwoDimensionalBoardGameViewer;
import boardGame.agents.BoardGamePlayer;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class BoardGameUtil {
	@SuppressWarnings("rawtypes")
	static TwoDimensionalBoardGameViewer view = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<Pair<double[], double[]>> playGame(BoardGame bg, BoardGamePlayer[] players, BoardGameFitnessFunction fit){
		
		
		bg.reset();
		double[] fitStore = new double[players.length];
		
		if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Creates a new BoardGameViewer if bg is a TwoDimensionalBoardGame
			if(view != null){ 
				view.panel.dispose();
				view = null;
			}
			view = new TwoDimensionalBoardGameViewer((TwoDimensionalBoardGame) bg);
		}
		
		while(!bg.isGameOver()){

			if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders each Move in the game
				view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
			}
			if(Parameters.parameters.booleanParameter("stepByStep")){
				System.out.print("Press enter to continue");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
			
			int playIndex = bg.getCurrentPlayer(); // Stores the current Player's Index to access the Player's Fitness
			bg.move(players[bg.getCurrentPlayer()]);
			
			fitStore[playIndex] += fit.updateFitness(bg.getCurrentState(), playIndex); // Updates the Fitness for the Player that just made a Move
		}
		
		if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders the last Move of the game
			view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
			if(Parameters.parameters.booleanParameter("stepByStep")){
				System.out.print("Press enter to continue");
				MiscUtil.waitForReadStringAndEnterKeyPress();
			}
		}
		
		ArrayList<Pair<double[], double[]>> scoring = new ArrayList<Pair<double[], double[]>>(bg.getNumPlayers());
		
		for(int i = 0; i < players.length; i++){
			double fitness = fitStore[i]; // Gets the final Fitness for each Player

			Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] { fitness }, new double[0]);
			scoring.add(evalResults);
		}
		
		return scoring; // Returns the Fitness of the individual's Genotype<T>
	}
	
	
	@SuppressWarnings("rawtypes")
	public static List<Substrate> getSubstrateInformation(BoardGame bg) {
			TwoDimensionalBoardGame temp = (TwoDimensionalBoardGame) bg;
			int height = temp.getStartingState().getBoardHeight();
			int width = temp.getStartingState().getBoardWidth();
			List<Substrate> substrateInformation = new LinkedList<Substrate>();
			Substrate boardInputs = new Substrate(new Pair<Integer, Integer>(width, height), 
			Substrate.INPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.INPUT_SUBSTRATE, 0), "Board Inputs");
			substrateInformation.add(boardInputs);
			Substrate processing = new Substrate(new Pair<Integer, Integer>(width, height), 
			Substrate.PROCCESS_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.PROCCESS_SUBSTRATE, 0), "Processing");
			substrateInformation.add(processing);
			Substrate output = new Substrate(new Pair<Integer, Integer>(1, 1), // Single utility value
			Substrate.OUTPUT_SUBSTRATE, new Triple<Integer, Integer, Integer>(0, Substrate.OUTPUT_SUBSTRATE, 0), "Utility Output");
			substrateInformation.add(output);
			// Otherwise, no substrates will be defined, and the code will crash from the null result
		
		return substrateInformation;
	}

	// Used for Hyper-NEAT

	public static List<Pair<String, String>> getSubstrateConnectivity() {
			List<Pair<String, String>> substrateConnectivity = new LinkedList<Pair<String, String>>();
			substrateConnectivity.add(new Pair<String, String>("Board Inputs", "Processing"));
			substrateConnectivity.add(new Pair<String, String>("Processing", "Utility Output"));	
			if(Parameters.parameters.booleanParameter("extraHNLinks")) {
				substrateConnectivity.add(new Pair<String, String>("Board Inputs", "Utility Output"));
			}
		
		return substrateConnectivity;
	}
	
}
