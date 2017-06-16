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
import edu.utexas.cs.nn.networks.hyperneat.HyperNEATUtil;
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
		List<Triple<String, Integer, Integer>> outputInfo = new LinkedList<Triple<String, Integer, Integer>>();
		outputInfo.add(new Triple<String, Integer, Integer>("Processing", 0, Substrate.OUTPUT_SUBSTRATE));
		// Otherwise, no substrates will be defined, and the code will crash from the null result

		return HyperNEATUtil.getSubstrateInformation(width, height, 1, outputInfo); // Only has 1 Input Substrate with the Height and Width of the Board Game
	}

	// Used for Hyper-NEAT
	public static List<Triple<String, String, Boolean>> getSubstrateConnectivity() {
		List<String> outputNames = new LinkedList<String>();
		outputNames.add("Utility Output");	
		
		return HyperNEATUtil.getSubstrateConnectivity(1, outputNames); // Only has 1 Input Substrate
	}
	
}
