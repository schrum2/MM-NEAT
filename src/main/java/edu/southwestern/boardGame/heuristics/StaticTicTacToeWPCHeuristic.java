package edu.southwestern.boardGame.heuristics;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.TwoDimensionalBoardGameState;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard;
import edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.ttt.TicTacToe;
import edu.southwestern.boardGame.ttt.TicTacToeState;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class StaticTicTacToeWPCHeuristic<T extends TwoDimensionalBoardGameState> extends WeightedPieceCounterHeuristic<T> {


	// Stupid TicTacToe Player for testing purposes
	private static final double[][] WEIGHTS = new double[][]{{10000, 5000, 1000},
															 {  500,  100,   50},
															 {   10,    5,    1}};
															 
	public StaticTicTacToeWPCHeuristic() {
		super(WEIGHTS);
	}
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.ttt.TicTacToe", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticTicTacToeWPCHeuristic",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:8"});
		
		MMNEAT.loadClasses();
		
		TicTacToe bg = null;
		
		try {
			bg = (TicTacToe) ClassCreation.createObject("boardGame");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		@SuppressWarnings("unchecked")
		BoardGamePlayer<TicTacToeState>[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<TicTacToeState>(), new BoardGamePlayerHuman2DBoard<TicTacToeState>()};
		
		List<BoardGameFitnessFunction<TicTacToeState>> scores = new ArrayList<BoardGameFitnessFunction<TicTacToeState>>();
		scores.add(new SimpleWinLoseDrawBoardGameFitness<TicTacToeState>());
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<BoardGameFitnessFunction<TicTacToeState>>()); // No Other Scores
		System.out.println("Game Over: Press Enter");
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
}
