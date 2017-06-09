package boardGame.checkers;

import java.util.ArrayList;
import java.util.List;

import boardGame.BoardGame;
import boardGame.TwoDimensionalBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import boardGame.othello.OthelloState;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;

public class Checkers extends TwoDimensionalBoardGame<CheckersState> {
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.checkers.Checkers", "boardGameOpponent:boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:boardGame.heuristics.PieceDifferentialBoardGameHeuristic",
				"boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true"});
		
		MMNEAT.loadClasses();
		
		BoardGame bg = null;
		
		try {
			bg = (BoardGame) ClassCreation.createObject("boardGame");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		BoardGamePlayer[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState>(), new BoardGamePlayerHuman2DBoard<OthelloState>()};
		
		List<BoardGameFitnessFunction> scores = new ArrayList<BoardGameFitnessFunction>();
		scores.add(new SimpleWinLoseDrawBoardGameFitness<OthelloState>());
		
		BoardGameUtil.playGame(bg, players, scores);
		System.out.println("Game Over");
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
