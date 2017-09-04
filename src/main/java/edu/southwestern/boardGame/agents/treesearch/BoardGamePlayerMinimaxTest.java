package boardGame.agents.treesearch;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import boardGame.agents.BoardGamePlayer;
import boardGame.heuristics.StaticTicTacToeWPCHeuristic;
import boardGame.ttt.TicTacToe;
import boardGame.ttt.TicTacToeState;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class BoardGamePlayerMinimaxTest {

	private static final int X = TicTacToeState.X;
	private static final int O = TicTacToeState.O;
	private static final int E = TicTacToeState.EMPTY;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.connectFour.ConnectFour", "boardGameOpponent:edu.southwestern.boardGame.agents.BoardGamePlayerHuman2DBoard",
				"boardGamePlayer:edu.southwestern.boardGame.agents.treesearch.BoardGamePlayerMinimaxAlphaBetaPruning", "watch:true"});
		
		MMNEAT.loadClasses();
		
		TicTacToe test = new TicTacToe();
		@SuppressWarnings("rawtypes")
		BoardGamePlayer[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimax(new StaticTicTacToeWPCHeuristic()), new BoardGamePlayerMinimax(new StaticTicTacToeWPCHeuristic())};
		int play = 0;
		while(!test.isGameOver()){
			test.move(players[play]);
			play = (play + 1) % 2;
		}
		
		int[][] board = new int[][]{{X,O,X},
									{O,X,O},
									{X,E,E}};
		List<Integer> win = new ArrayList<Integer>();
		win.add(X);
									
		assertEquals(test.getCurrentState(), new TicTacToeState(board, O, win));
	}

}
