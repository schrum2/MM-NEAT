package boardGame.agents.treesearch;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import boardGame.*;
import boardGame.agents.*;
import boardGame.featureExtractor.BoardGameFeatureExtractor;
import boardGame.heuristics.*;
import boardGame.othello.*;
import edu.utexas.cs.nn.util.ClassCreation;

public class BoardGamePlayerMinimaxAlphaBetaPruningTest {
	
	Othello bg1 = new Othello();
	Othello bg2 = new Othello();
	
	BoardGamePlayerRandom randomPlayer = new BoardGamePlayerRandom<BoardGameState>();
	
	@SuppressWarnings("unchecked")
	BoardGamePlayer[] players = new BoardGamePlayer[]{randomPlayer, null};
	
	BoardGameHeuristic bgh = new PieceDifferentialBoardGameHeuristic();
	
	BoardGamePlayerMinimax mini = new BoardGamePlayerMinimax(bgh);
	BoardGamePlayerMinimaxAlphaBetaPruning alpha = new BoardGamePlayerMinimaxAlphaBetaPruning(bgh);
	
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		
		for(int i = 0; i <= 100; i++){
			randomPlayer.setRandomSeed(i);
			
			bg1.reset();
			bg2.reset();
			
			players[1] = mini;
			while(!bg1.isGameOver()){
				bg1.move(players[bg1.getCurrentPlayer()]);
			}
			
			players[1] = mini;
			while(!bg2.isGameOver()){
				bg2.move(players[bg2.getCurrentPlayer()]);
			}
			
			assertEquals(bg1.getCurrentState(), bg2.getCurrentState());
			
		}
	}

}