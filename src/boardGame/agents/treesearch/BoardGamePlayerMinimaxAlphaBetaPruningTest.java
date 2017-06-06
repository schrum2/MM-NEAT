package boardGame.agents.treesearch;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerRandom;
import boardGame.heuristics.BoardGameHeuristic;
import boardGame.heuristics.PieceDifferentialBoardGameHeuristic;
import boardGame.othello.Othello;
import boardGame.othello.OthelloState;

public class BoardGamePlayerMinimaxAlphaBetaPruningTest {
	
	Othello bg1 = new Othello();
	Othello bg2 = new Othello();
	
	BoardGamePlayerRandom<OthelloState> randomPlayer = new BoardGamePlayerRandom<OthelloState>();
	
	@SuppressWarnings("unchecked")
	BoardGamePlayer<OthelloState>[] players = new BoardGamePlayer[]{randomPlayer, null};
	
	BoardGameHeuristic<OthelloState> bgh = new PieceDifferentialBoardGameHeuristic<OthelloState>();
	
	BoardGamePlayerMinimax<OthelloState> mini = new BoardGamePlayerMinimax<OthelloState>(bgh);
	BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState> alpha = new BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState>(bgh);
		
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
			
			assertEquals(bg1.getCurrentState(), bg2.getCurrentState());
			
		}
	}

}