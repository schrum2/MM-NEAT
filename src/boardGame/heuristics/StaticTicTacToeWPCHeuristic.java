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



	private static final double[][] WEIGHTS = new double[][]{{10000, 5000, 1000},
															 {  500,  100,   50},
															 {   10,    5,    1}};
															 
	/**
	 * Weights for Othello Heuristic from:
	 * 
	 * Temporal Difference Learning Versus Co-Evolution for
	 * Acquiring Othello Position Evaluation
	 * 
	 * (Simon M. Lucas, Thomas P. Runarsson)
	 * 
	 * 
	 * http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=10AB4B0966FEE51BE133255498065C42?doi=10.1.1.580.8400&rep=rep1&type=pdf											 
	 */
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
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<>()); // No Other Scores
		System.out.println("Game Over: Press Enter");
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
}
