package boardGame.checkers;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.heuristics.PieceDifferentialBoardGameHeuristic;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class Checkers extends TwoDimensionalBoardGame<CheckersState> {
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.checkers.Checkers", "boardGameOpponent:boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:boardGame.heuristics.PieceDifferentialBoardGameHeuristic",
				"boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:2"});
		
		MMNEAT.loadClasses();
		
		Checkers bg = null;
		
		try {
			bg = (Checkers) ClassCreation.createObject("boardGame");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		@SuppressWarnings("unchecked")
		BoardGamePlayer<CheckersState>[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<CheckersState>(new PieceDifferentialBoardGameHeuristic<CheckersState>()), new BoardGamePlayerHuman2DBoard<CheckersState>()};
		
		List<BoardGameFitnessFunction<CheckersState>> scores = new ArrayList<BoardGameFitnessFunction<CheckersState>>();
		scores.add(new SimpleWinLoseDrawBoardGameFitness<CheckersState>());
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<BoardGameFitnessFunction<CheckersState>>()); // No Other Scores
		System.out.println("Game Over. Press enter");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
	public Checkers(){
		super(new CheckersState());
	}
	
	/**
	 * Returns the number of Players for Checkers
	 * 
	 * @return 2, the number of Players for Checkers
	 */
	@Override
	public int getNumPlayers() {
		return 2;
	}

	/**
	 * Returns a String containing the name of the Game, "Checkers"
	 * 
	 * @return String containing "Checkers"
	 */
	@Override
	public String getName() {
		return "Checkers";
	}
	
}
