package edu.southwestern.tasks.boardGame;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import boardGame.BoardGameState;
import boardGame.TestBoardGame;
import boardGame.agents.BoardGamePlayer;
import boardGame.agents.BoardGamePlayerRandom;
import boardGame.checkers.CheckersState;
import boardGame.fitnessFunction.BoardGameFitnessFunction;
import boardGame.fitnessFunction.SimpleWinLoseDrawBoardGameFitness;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.Pair;

public class BoardGameUtilBGAvgScoresTest<T extends BoardGameState> {
	
	List<BoardGameFitnessFunction<T>> fit = new ArrayList<BoardGameFitnessFunction<T>>();
	BoardGamePlayer<T>[] players;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		fit.add(new SimpleWinLoseDrawBoardGameFitness<T>());
		// Doesn't matter which players are playing; TestBoardGame is always an End State and Player 1 wins
		players = new BoardGamePlayer[]{new BoardGamePlayerRandom<T>(), new BoardGamePlayerRandom<T>()};
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.othello.Othello", "minimaxSearchDepth:2"});
		MMNEAT.loadClasses();
	}

	@Test
	public void testPlayGame() {
		// Results should have 2 items, one for each Player.
		ArrayList<Pair<double[], double[]>> results = BoardGameUtil.playGame(new TestBoardGame<T>(), players, fit, new ArrayList<BoardGameFitnessFunction<T>>()); // No Other Scores
		for(Pair<double[], double[]> scores : results){
			assertEquals((1-2)/2, scores.t1[0], 0.00001); // Average score is 1 Win, 1 Lose / 2 Matches; (1-2)/2
		}
		
	}

}
