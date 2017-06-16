package edu.utexas.cs.nn.tasks.boardGame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.BoardGameState;
import boardGame.TwoDimensionalBoardGame;
import boardGame.TwoDimensionalBoardGameState;
import boardGame.TwoDimensionalBoardGameViewer;
import boardGame.agents.BoardGamePlayer;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.networks.hyperneat.Substrate;
import edu.utexas.cs.nn.parameters.CommonConstants;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class BoardGameUtil {
	@SuppressWarnings("rawtypes")
	static TwoDimensionalBoardGameViewer view = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends BoardGameState> ArrayList<Pair<double[], double[]>> playGame(BoardGame<T> bg, BoardGamePlayer<T>[] players, List<BoardGameFitnessFunction<T>> fit){
		bg.reset();
		
		for(BoardGameFitnessFunction fitFunct : fit){
			fitFunct.reset();
		}
		
		if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Creates a new BoardGameViewer if bg is a TwoDimensionalBoardGame
			view = MMNEAT.boardGameViewer;
		}
		
		for(int i = 0; i < bg.getNumPlayers(); i++){ // Cycles through the number of possible Player positions
		
			while(!bg.isGameOver()){

				if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders each Move in the game
					view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
				}
				if(Parameters.parameters.booleanParameter("stepByStep")){
					System.out.print("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
				
				int playIndex = (bg.getCurrentPlayer() + i) % bg.getNumPlayers(); // Stores the current Player's Index to access the Player's Fitness; the Player's cycle through the different Player positions
				bg.move(players[playIndex]);
			
				for(BoardGameFitnessFunction fitFunct : fit){
					fitFunct.updateFitness(bg.getCurrentState(), playIndex);
				}
			}
			
			// TODO: The fitness processing code below needs to be moved here, and then you also need to account
			// for averaging the fitness values across the multiple games (talk to Alice about how this was done
			// in MicroRTS.
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
			
			double fitness = (fit.get(0).getFitness(players[i]) / bg.getNumPlayers()); // Averages the total accumulated Fitness over the number of games played
			
			double[] otherScores = new double[players.length]; // Initialized to empty to avoid errors
			
			if(fit.size() > 1){ // Has at least 1 Other Score; must track their fitness
				otherScores = new double[fit.size()-1]; // Stores all other Scores except the first, which is used as the Selection Fitness
				
				for(int j = 1; j < fit.size(); j++){
					otherScores[j-1] = (fit.get(j).getFitness(players[i]) / bg.getNumPlayers()); // Gets all Scores except the first one; averages the total fitness over the number of games played
				}
			}
			
			Pair<double[], double[]> evalResults = new Pair<double[], double[]>(new double[] { fitness }, otherScores);
			scoring.add(evalResults);
		}
		
		if(CommonConstants.watch){ // Prints out the list of Winners at the end of a visual evaluation
			System.out.println("Winner(s): " + bg.getWinners());
		}
		
		return scoring; // Returns the Fitness of the individual's Genotype<T>
	}
	
	public static <S extends TwoDimensionalBoardGameState> List<Substrate> getSubstrateInformation(BoardGame<S> bg) {
		TwoDimensionalBoardGame<S> temp = (TwoDimensionalBoardGame<S>) bg;
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
	public static List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		List<Triple<String, String, Boolean>> substrateConnectivity = new LinkedList<Triple<String, String, Boolean>>();
		substrateConnectivity.add(new Triple<String, String, Boolean>("Board Inputs", "Processing", Boolean.FALSE));
		substrateConnectivity.add(new Triple<String, String, Boolean>("Processing", "Utility Output", Boolean.FALSE));	
		if(Parameters.parameters.booleanParameter("extraHNLinks")) {
			substrateConnectivity.add(new Triple<String, String, Boolean>("Board Inputs", "Utility Output", Boolean.FALSE));
		}
		return substrateConnectivity;
	}
	
}
