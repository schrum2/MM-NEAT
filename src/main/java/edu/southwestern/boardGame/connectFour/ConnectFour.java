package boardGame.connectFour;

import java.util.ArrayList;
import java.util.List;

import boardGame.TwoDimensionalBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerHuman2DBoard;
import boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning;
import boardGame.checkers.CheckersState;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.boardGame.BoardGameUtil;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;

public class ConnectFour extends TwoDimensionalBoardGame<ConnectFourState>{
	
	public static void main(String[] args){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.connectFour.ConnectFour", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true"});
		
		MMNEAT.loadClasses();
		
		ConnectFour bg = null;
		
		try {
			bg = (ConnectFour) ClassCreation.createObject("boardGame");
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		@SuppressWarnings("unchecked")
		BoardGamePlayer<ConnectFourState>[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<ConnectFourState>(), new BoardGamePlayerHuman2DBoard<ConnectFourState>()};
		
		List<BoardGameFitnessFunction<ConnectFourState>> scores = new ArrayList<BoardGameFitnessFunction<ConnectFourState>>();
		scores.add(new SimpleWinLoseDrawBoardGameFitness<ConnectFourState>());
		
		BoardGameUtil.playGame(bg, players, scores, new ArrayList<BoardGameFitnessFunction<ConnectFourState>>()); // No Other Scores
		System.out.println("Game Over: Press Enter");
		
		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		MMNEAT.boardGameViewer.close();
	}
	
	public ConnectFour(){
		super(new ConnectFourState());
	}

	ConnectFour(ConnectFourState state){
		super(state);
	}
	
	@Override
	public int getNumPlayers() {
		return 2;
	}

	@Override
	public String getName() {
		return "Connect Four";
	}
	
	
	
}
