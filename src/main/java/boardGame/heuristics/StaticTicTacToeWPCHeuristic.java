package boardGame.heuristics;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.ttt.TicTacToe;
import boardGame.ttt.TicTacToeState;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.MiscUtil;

public class StaticTicTacToeWPCHeuristic<T extends TwoDimensionalBoardGameState> extends WeightedPieceCounterHeuristic<T> {


	// Stupid TicTacToe Player for testing purposes
	private static final double[][] WEIGHTS = new double[][]{{10000, 5000, 1000},
															 {  500,  100,   50},
															 {   10,    5,    1}};
															 
	public StaticTicTacToeWPCHeuristic() {
		super(WEIGHTS);
	}
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.ttt.TicTacToe", "boardGameOpponent:boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:boardGame.heuristics.StaticTicTacToeWPCHeuristic",
				"boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:8"});
		
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
