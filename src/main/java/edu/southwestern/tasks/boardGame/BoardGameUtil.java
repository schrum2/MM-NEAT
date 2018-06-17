package edu.southwestern.tasks.boardGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import edu.southwestern.boardGame.BoardGame;
import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.boardGame.TwoDimensionalBoardGame;
import edu.southwestern.boardGame.TwoDimensionalBoardGameState;
import edu.southwestern.boardGame.TwoDimensionalBoardGameViewer;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerRandom;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.OthelloPieceFitness;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.networks.hyperneat.HyperNEATUtil;
import edu.southwestern.networks.hyperneat.Substrate;
import edu.southwestern.networks.hyperneat.architecture.SubstrateConnectivity;
import edu.southwestern.parameters.CommonConstants;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.NoisyLonerTask;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.datastructures.Triple;

public class BoardGameUtil {
	@SuppressWarnings("rawtypes")
	static TwoDimensionalBoardGameViewer view = null;
	private static boolean stepByStep = Parameters.parameters.booleanParameter("stepByStep");
	private static boolean printFitness = Parameters.parameters.booleanParameter("printFitness");
	private static int openingRandomMoves = Parameters.parameters.integerParameter("boardGameOpeningRandomMoves");
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends BoardGameState> ArrayList<Pair<double[], double[]>> playGame(BoardGame<T> bg, BoardGamePlayer<T>[] players, List<BoardGameFitnessFunction<T>> fitScores, List<BoardGameFitnessFunction<T>> otherFit){
		// This needs to reset every time in case increasing random moves are used
		openingRandomMoves = Parameters.parameters.integerParameter("boardGameOpeningRandomMoves");
		if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Creates a new BoardGameViewer if bg is a TwoDimensionalBoardGame
			view = MMNEAT.boardGameViewer;
		}

		double[][][] fitnesses = new double[bg.getNumPlayers()][bg.getNumPlayers()][fitScores.size()];
		
		double[][][] otherScores = new double[bg.getNumPlayers()][bg.getNumPlayers()][otherFit.size()];
		
		// Each player plays as 1st, 2nd, etc.
		for(int i = 0; i < bg.getNumPlayers(); i++){ // Cycles through the number of possible Player positions
			if(CommonConstants.watch){
				for(int j = 0; j < bg.getNumPlayers(); j++){
					System.out.println("Player " + (j+1) + ": " + players[j]);
				}
			}
			
			// Reset game and fitness functions before each play
			bg.reset();
			for(BoardGameFitnessFunction fitFunct : fitScores){
				fitFunct.reset();
			}
			for(BoardGameFitnessFunction fitFunct : otherFit){
				fitFunct.reset();
			}
			
			int moveCount = 0;
			while(!bg.isGameOver()){

				if(CommonConstants.watch && bg instanceof TwoDimensionalBoardGame){ // Renders each Move in the game
					view.reset((TwoDimensionalBoardGameState) bg.getCurrentState());
				}
//				if(stepByStep){
//					System.out.print("Press enter to continue");
//					MiscUtil.waitForReadStringAndEnterKeyPress();
//				}
				
				int playIndex = bg.getCurrentPlayer(); // Stores the current Player's Index to access the Player's Fitness
				//System.out.println(players[playIndex]);
				if(moveCount < openingRandomMoves) {
					// Opening random moves introduce useful non-determinism
					bg.move(new BoardGamePlayerRandom<T>());
				} else {
					// Actual move from actual player
					bg.move(players[playIndex]);
				}
				
				for(BoardGameFitnessFunction fitFunct : fitScores){
					fitFunct.updateFitness(bg.getCurrentState(), playIndex);
				}
				for(BoardGameFitnessFunction fitFunct : otherFit){
					fitFunct.updateFitness(bg.getCurrentState(), playIndex);
				}
				moveCount++;
			}

			if(CommonConstants.watch || printFitness){ // Prints out the list of Winners at the end of a visual evaluation
				System.out.println("Winner(s): " + bg.getWinners());
				if(bg instanceof TwoDimensionalBoardGame){
					for(int j = 0; j < bg.getNumPlayers(); j++){
						System.out.println(players[j]);
						System.out.println("Player " + (j+1) + " Pieces: " + ((TwoDimensionalBoardGame) bg).pieceCount(j));
					}
				}
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
				// Cycles through the Selection Scores
				for(int j = 0; j < fitScores.size(); j++){
					fitnesses[k][i][j] = (1.0*fitScores.get(j).getFitness(players[playerIndex], playerIndex));
				}
				// Stores all other Scores except the Selection Scores
				for(int j = 0; j < otherFit.size(); j++){
					otherScores[k][i][j] = (1.0*otherFit.get(j).getFitness(players[playerIndex], playerIndex));
				}

				try{
					// Make sure OthelloPieceFitness and win/loss/draw are consistent
					// Relies on implication: A -> B equivalent to !A || B
					assert !(fitScores.get(0) instanceof OthelloPieceFitness) || (!(fitnesses[k][i][0] > 0) || otherScores[k][i][0] == 1) : "Positive piece count should be a win in Othello: " + Arrays.toString(fitnesses[k][i]) + Arrays.toString(otherScores[k][i]);
					assert !(fitScores.get(0) instanceof OthelloPieceFitness) || (!(fitnesses[k][i][0] < 0) || otherScores[k][i][0] == -2) : "Negative piece count should be a loss in Othello: " + Arrays.toString(fitnesses[k][i]) + Arrays.toString(otherScores[k][i]);
					assert !(fitScores.get(0) instanceof OthelloPieceFitness) || (!(fitnesses[k][i][0] == 0) || otherScores[k][i][0] == 0) : "Zero piece count should be a tie in Othello: " + Arrays.toString(fitnesses[k][i]) + Arrays.toString(otherScores[k][i]);
				}catch(AssertionError e) {
					System.out.println(e);
					System.out.println(bg);
					System.out.println(Arrays.deepToString(fitnesses));
					System.out.println(Arrays.deepToString(otherScores));
					MiscUtil.waitForReadStringAndEnterKeyPress();
				}
			}
			
			if (MMNEAT.evalReport != null) {
				MMNEAT.evalReport.log("   Match " + (i+1) + ": \n");
				for(int j = 0; j < bg.getNumPlayers(); j++){ // Cycles through the Players
					int playerIndex = (j+i) % bg.getNumPlayers();
					MMNEAT.evalReport.log("\tPlayer " + (playerIndex+1) + ": " + players[playerIndex]);
					for(int k = 0; k < fitScores.size(); k++){ // Cycles through the Other Scores
						MMNEAT.evalReport.log("\t   Fitness Score: " + fitScores.get(k).getFitnessName() + ": " + fitnesses[j][i][k]);
					}
					for(int k = 0; k < otherFit.size(); k++){ // Cycles through the Other Scores
						MMNEAT.evalReport.log("\t   Other Score: " + otherFit.get(k).getFitnessName() + ": " + otherScores[j][i][k]);
					}
					MMNEAT.evalReport.log(""); // Creates some space between Players
				}
				MMNEAT.evalReport.log("\n\tWinners (by Index): " + bg.getWinners()); // Logs Winners
				if(bg instanceof TwoDimensionalBoardGame){
					for(int j = 0; j < bg.getNumPlayers(); j++){
						MMNEAT.evalReport.log("\tPlayer " + (j+1) + " Pieces: " + ((TwoDimensionalBoardGame) bg).pieceCount(j));
					}
				}
				MMNEAT.evalReport.log("\n"); // Creates some space between Matches
			}
			
			// Restore original watch value
			CommonConstants.watch = originalWatch;
			stepByStep = originalStepByStep;
			// Shift the player order
			ArrayUtil.rotateRight(players, 1);
		}
		
		if (MMNEAT.evalReport != null){
			MMNEAT.evalReport.log("\n\n"); // Creates some space between Evals
		}
		
//		System.out.println(Arrays.deepToString(fitnesses));
//		System.out.println(Arrays.deepToString(otherScores));
		
		ArrayList<Pair<double[], double[]>> scoring = new ArrayList<Pair<double[], double[]>>(bg.getNumPlayers());
		for(int k = 0; k < players.length; k++){
			if(printFitness) {
				System.out.println("Fitness for player " + k + ": " + Arrays.deepToString(fitnesses[k])+Arrays.deepToString(otherScores[k]));
			}
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
	public static List<SubstrateConnectivity> getSubstrateConnectivity() {
		List<String> outputNames = new LinkedList<String>();
		outputNames.add("Utility Output");	
		
		return HyperNEATUtil.getSubstrateConnectivity(1, outputNames); // Only has 1 Input Substrate
	}
	
}
