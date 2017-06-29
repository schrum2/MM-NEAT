package boardGame.heuristics;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.othello.Othello;
import boardGame.othello.OthelloState;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.MiscUtil;

public class StaticOthelloWPCHeuristic<T extends TwoDimensionalBoardGameState> extends WeightedPieceCounterHeuristic<T> {



	private static final double[][] WEIGHTS = new double[][]{{ 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00},
															 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
															 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
															 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
															 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
															 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
															 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
															 { 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00}};
															 
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
	public StaticOthelloWPCHeuristic() {
		super(WEIGHTS);
	}
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.othello.Othello", "boardGameOpponent:boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:boardGame.heuristics.StaticOthelloWPCHeuristic",
				"boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:8"});
		
		MMNEAT.loadClasses();
		
		Othello bg = null;
		
		try {
			bg = (Othello) ClassCreation.createObject("boardGame");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		@SuppressWarnings("unchecked")
		BoardGamePlayer<OthelloState>[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState>(), new BoardGamePlayerHuman2DBoard<OthelloState>()};
		
		List<BoardGameFitnessFunction<OthelloState>> scores = new ArrayList<BoardGameFitnessFunction<OthelloState>>();
		scores.add(new SimpleWinLoseDrawBoardGameFitness<OthelloState>());
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<>()); // No Other Scores
		System.out.println("Game Over: Press Enter");
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
}
