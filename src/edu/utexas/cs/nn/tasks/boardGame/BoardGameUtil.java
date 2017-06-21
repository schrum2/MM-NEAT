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
import edu.utexas.cs.nn.tasks.NoisyLonerTask;
import edu.utexas.cs.nn.util.MiscUtil;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import edu.utexas.cs.nn.util.datastructures.Pair;
import edu.utexas.cs.nn.util.datastructures.Triple;

public class BoardGameUtil {
	@SuppressWarnings("rawtypes")
	static TwoDimensionalBoardGameViewer view = null;
	private static boolean stepByStep = Parameters.parameters.booleanParameter("stepByStep");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends BoardGameState> ArrayList<Pair<double[], double[]>> playGame(BoardGame<T> bg, BoardGamePlayer<T>[] players, List<BoardGameFitnessFunction<T>> fit){
		if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Creates a new BoardGameViewer if bg is a TwoDimensionalBoardGame
			view = MMNEAT.boardGameViewer;
		}

		double[][][] fitnesses = new double[bg.getNumPlayers()][bg.getNumPlayers()][1];
		// First score in fit is actual fitness, and the rest are "other" scores
		double[][][] otherScores = new double[bg.getNumPlayers()][bg.getNumPlayers()][fit.size() - 1];
		
		// Each player plays as 1st, 2nd, etc.
		for(int i = 0; i < bg.getNumPlayers(); i++){ // Cycles through the number of possible Player positions
			if(CommonConstants.watch){
				for(int j = 0; j < bg.getNumPlayers(); j++){
					System.out.println("Player " + (j+1) + ": " + players[j]);
				}
			}
			
			// Reset game and fitness functions before each play
			bg.reset();
			for(BoardGameFitnessFunction fitFunct : fit){
				fitFunct.reset();
			}
			
			while(!bg.isGameOver()){

				if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders each Move in the game
					view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
				}
				if(stepByStep){
					//System.out.print("Press enter to continue");
					//MiscUtil.waitForReadStringAndEnterKeyPress();
				}
				
				int playIndex = bg.getCurrentPlayer(); // Stores the current Player's Index to access the Player's Fitness
				//System.out.println(players[playIndex]);
				bg.move(players[playIndex]);
			
				for(BoardGameFitnessFunction fitFunct : fit){
					fitFunct.updateFitness(bg.getCurrentState(), playIndex);
				}
			}

			if(CommonConstants.watch){ // Prints out the list of Winners at the end of a visual evaluation
				System.out.println("Winner(s): " + bg.getWinners());
			}
			
			if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders the last Move of the game
				view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
				if(stepByStep){
					System.out.println("Game Over!");
					System.out.print("Press enter to continue");
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			}	

			// If the other score of playing against a static opponent is used, then don't watch those games
			boolean originalWatch = CommonConstants.watch;
			CommonConstants.watch = false;
			boolean originalStepByStep = stepByStep;
			stepByStep = false;
			// For each player in the game, save fitness and other scores
			for(int k = 0; k < players.length; k++){
				int playerIndex = (k+i) % bg.getNumPlayers();
				fitnesses[k][i][0] = (1.0*fit.get(0).getFitness(players[k], playerIndex));
				if(fit.size() > 1){ // Has at least 1 Other Score; must track their fitness
					// Stores all other Scores except the first, which is used as the Selection Fitness
					for(int j = 1; j < fit.size(); j++){
						otherScores[k][i][j-1] = (1.0*fit.get(j).getFitness(players[k], playerIndex)); // Gets all Scores except the first one
					}
				}
			}
			// Restore original watch value
			CommonConstants.watch = originalWatch;
			stepByStep = originalStepByStep;
			// Shift the player order
			ArrayUtil.rotateRight(players, 1);
		}
		
//		System.out.println(Arrays.deepToString(fitnesses));
//		System.out.println(Arrays.deepToString(otherScores));
		
		ArrayList<Pair<double[], double[]>> scoring = new ArrayList<Pair<double[], double[]>>(bg.getNumPlayers());
		for(int k = 0; k < players.length; k++){
			// Average across the games played as 1st, 2nd player etc.
			Pair<double[], double[]> evalResults = NoisyLonerTask.averageResults(fitnesses[k], otherScores[k]);
			scoring.add(evalResults);
		}		
		
		return scoring; // Returns the Fitness of the individual's Genotype<T>
	}
	
	public static <S extends TwoDimensionalBoardGameState> List<Substrate> getSubstrateInformation(BoardGame<S> bg) {
		TwoDimensionalBoardGame<S> temp = (TwoDimensionalBoardGame<S>) bg;
		int height = temp.getStartingState().getBoardHeight();
		int width = temp.getStartingState().getBoardWidth();
		List<Triple<String, Integer, Integer>> outputInfo = new LinkedList<Triple<String, Integer, Integer>>();
		outputInfo.add(new Triple<String, Integer, Integer>("Utility Output", 1, 1));
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
