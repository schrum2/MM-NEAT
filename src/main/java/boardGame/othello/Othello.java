package boardGame.othello;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.tasks.boardGame.BoardGameUtil;
import edu.utexas.cs.nn.util.ClassCreation;
import edu.utexas.cs.nn.util.MiscUtil;

public class Othello extends TwoDimensionalBoardGame<OthelloState>{
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.othello.Othello", "boardGameOpponent:boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGameOpponentHeuristic:boardGame.heuristics.StaticOthelloWPCHeuristic",
				"boardGamePlayer:boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true", "minimaxSearchDepth:6"});
		
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
