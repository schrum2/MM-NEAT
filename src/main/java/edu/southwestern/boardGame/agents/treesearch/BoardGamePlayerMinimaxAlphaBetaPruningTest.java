package edu.southwestern.boardGame.agents.treesearch;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.boardGame.agents.BoardGamePlayer;
import edu.southwestern.boardGame.agents.BoardGamePlayerRandom;
import edu.southwestern.boardGame.heuristics.BoardGameHeuristic;
import edu.southwestern.boardGame.heuristics.StaticOthelloWPCHeuristic;
import edu.southwestern.boardGame.othello.Othello;
import edu.southwestern.boardGame.othello.OthelloState;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;

public class BoardGamePlayerMinimaxAlphaBetaPruningTest {
	
	Othello bg1;
	Othello bg2;
	
	BoardGamePlayerRandom<OthelloState> randomPlayer = new BoardGamePlayerRandom<OthelloState>();
	
	@SuppressWarnings("unchecked")
	BoardGamePlayer<OthelloState>[] players = new BoardGamePlayer[]{randomPlayer, null};
	
	BoardGameHeuristic<OthelloState> bgh;
	
	BoardGamePlayerMinimax<OthelloState> mini;
	BoardGamePlayerMinimax<OthelloState> alpha;
		
	@Before
	public void setup(){
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.othello.Othello", "minimaxSearchDepth:2", 
				"randomArgMaxTieBreak:false"});
		MMNEAT.loadClasses();
		
		bg1 = new Othello();
		bg2 = new Othello();
		bgh = new StaticOthelloWPCHeuristic();
		mini = new BoardGamePlayerMinimax<OthelloState>(bgh);
		alpha = new BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState>(bgh);
	}
	
	@Test
	public void test() {

		for(int i = 0; i <= 100; i++){

			randomPlayer.setRandomSeed(i);
			bg1.reset();			
			players[1] = mini;
			while(!bg1.isGameOver()){
				bg1.move(players[bg1.getCurrentPlayer()]);
			}
			
			randomPlayer.setRandomSeed(i);
			bg2.reset();
			players[1] = alpha;
			while(!bg2.isGameOver()){
				bg2.move(players[bg2.getCurrentPlayer()]);
			}
			assertEquals(bg1.getCurrentState(), bg2.getCurrentState()); // TODO: Incorrect here. Why?
			
			//System.out.println("Done " + i);
		}
	}

}