package edu.southwestern.boardGame.heuristics;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard;
import edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.boardGame.othello.Othello;
import edu.southwestern.boardGame.othello.OthelloState;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class StaticOthelloWPCHeuristic extends WeightedPieceCounterHeuristic<OthelloState> {



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
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.othello.Othello", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:8"});
		
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
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<BoardGameFitnessFunction<OthelloState>>()); // No Other Scores
		System.out.println("Game Over: Press Enter");
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
}
