package boardGame.agents.treesearch;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import boardGame.BoardGame;
import boardGame.BoardGameState;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerRandom;
import boardGame.heuristics.PieceDifferentialBoardGameHeuristic;
import boardGame.othello.Othello;

public class BoardGamePlayerMinimaxAlphaBetaPruningTest {
	
	BoardGame bg1;
	BoardGame bg2;
	BoardGamePlayerRandom random;
	BoardGamePlayerMinimax mini;
	BoardGamePlayerMinimaxAlphaBetaPruning alpha;
	BoardGamePlayer[] players;
	
	
	
	@Before
	public void setUp() throws Exception {
		bg1 = new Othello();
		bg2 = new Othello();
		random = new BoardGamePlayerRandom();
		mini = new BoardGamePlayerMinimax(new PieceDifferentialBoardGameHeuristic());
		alpha = new BoardGamePlayerMinimaxAlphaBetaPruning(new PieceDifferentialBoardGameHeuristic());
		players = new BoardGamePlayer[]{random, null};
	}
	
	@Test
	public void test() {

		for(int i = 0; i <= 100; i++){ // Cycles through the Random Seeds
			bg1.reset();
			players[1] = mini;
			while(!bg1.isGameOver()){
				bg1.move(players[bg1.getCurrentPlayer()]);
			}
			
			players[1] = alpha;
			bg2.reset();
			while(!bg2.isGameOver()){
				bg2.move(players[bg2.getCurrentPlayer()]);
			}
			
			assertEquals(bg1.getCurrentState(), bg2.getCurrentState());
			
		}
	}

}