package edu.southwestern.boardGame.othello;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.boardGame.TwoDimensionalBoardGame;
import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard;
import edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import edu.southwestern.boardGame.fitnessFunction.BoardGameFitnessFunction;
import edu.southwestern.boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class Othello extends TwoDimensionalBoardGame<OthelloState>{
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.othello.Othello", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:6"});
		
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
	
	/**
	 * Default Constructor
	 */
	public Othello(){
		super(new OthelloState());
	}
	
	Othello(OthelloState state){
		super(state);
	}
	
	/**
	 * Returns the number of Players for Othello
	 * 
	 * @return 2, the number of Players for Othello
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns a String containing the name of the Game
	 * 
	 * @return String containing "Othello/Reversi"
	 */
	@Override
	public String getName() {
		return "Othello/Reversi";
	}
	
}
