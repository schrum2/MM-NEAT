package boardGame.ttt;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class TicTacToe extends TwoDimensionalBoardGame<TicTacToeState> {

	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.ttt.TicTacToe", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:edu.southwestern.boardGame.heuristics.StaticOthelloWeightedPieceCounterHeursitic",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true"});
		
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
		System.out.println("Game Over. Press Enter");
		MiscUtil.waitForReadStringAndEnterKeyPress();
		MMNEAT.boardGameViewer.close();
	}
	
	public TicTacToe() {
		super(new TicTacToeState());
	}
	
	/**
	 * Returns the number of Players for Tic-Tac-Toe
	 * 
	 * @return 2, the number of Players
	 */
	@Override
	public int getNumPlayers() {
		return 2; // TicTacToe can only ever have two Players
	}

	/**
	 * Returns a String containing the name of the game, "Tic-Tac-Toe"
	 * 
	 * @return String containing "Tic-Tac-Toe"
	 */
	@Override
	public String getName() {
		return "Tic-Tac-Toe";
	}
	

	
}