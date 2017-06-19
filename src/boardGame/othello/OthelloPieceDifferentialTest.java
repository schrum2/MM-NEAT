package boardGame.othello;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import boardGame.agents.treesearch.BoardGamePlayerMinimax;
import boardGame.heuristics.PieceDifferentialBoardGameHeuristic;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.parameters.Parameters;

public class OthelloPieceDifferentialTest {

	Othello boardGame1;
	OthelloState test1;
	BoardGamePlayerMinimax<OthelloState> blackChip;

	private static final int E = OthelloState.EMPTY;
	private static final int B = OthelloState.BLACK_CHIP;
	private static final int W = OthelloState.WHITE_CHIP;
	
	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.utexas.cs.nn.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:boardGame.othello.Othello", "minimaxSearchDepth:3"});
		MMNEAT.loadClasses();
		
		blackChip = new BoardGamePlayerMinimax<OthelloState>(new PieceDifferentialBoardGameHeuristic<OthelloState>());
		
		int[][] board1 = new int[][]{{E,E,E,E,E,E,E,E},
									 {E,E,E,W,E,E,E,E},
									 {E,E,E,W,E,E,E,E},
									 {E,E,W,W,W,E,E,E},
									 {E,E,B,B,W,E,E,E},
									 {E,E,W,W,W,E,E,E},
									 {E,E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E,E}};
									 
		test1 = new OthelloState(board1, 0, new ArrayList<Integer>());
		
		boardGame1 = new Othello(test1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		// Checks that all possible States are detected
		
		Set<OthelloState> test1List = test1.possibleBoardGameStates(test1);	
		
		Set<OthelloState> test1States = new HashSet<OthelloState>();
		OthelloState correctChoice1 = new OthelloState(new int[][]{{E,E,E,E,E,E,E,E}, // Avoids obvious Piece grab because W could steal all the Pieces and win the Game; Depth == 2
																   {E,E,E,W,E,E,E,E},
																   {E,E,E,W,E,E,E,E},
																   {E,E,W,W,W,E,E,E},
																   {E,E,B,B,W,E,E,E},
																   {E,E,W,W,B,E,E,E},
																   {E,E,E,E,E,B,E,E},
																   {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>());
		
		test1States.add(correctChoice1);
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,E,W,E,B,E,E},
													 {E,E,W,W,B,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,B,B,B,B,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));		
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,B,E,E,E,E},
													 {E,E,E,B,E,E,E,E},
													 {E,E,E,B,E,E,E,E},
													 {E,E,W,B,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));		
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,B,W,E,E,E},
													 {E,E,E,B,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,B,W,W,E,E,E},
													 {E,B,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));

		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,B,E,W,E,E,E,E},
													 {E,E,B,W,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													  {E,E,E,W,E,E,E,E},
													  {E,E,E,W,B,E,E,E},
													  {E,E,W,B,W,E,E,E},
													  {E,E,B,B,W,E,E,E},
													  {E,E,W,W,W,E,E,E},
													  {E,E,E,E,E,E,E,E},
													  {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
			  										 {E,E,E,W,E,E,E,E},
			  										 {E,E,E,W,E,E,E,E},
			  										 {E,E,W,W,W,E,E,E},
			  										 {E,E,B,B,W,E,E,E},
			  										 {E,E,B,W,W,E,E,E},
			  										 {E,E,B,E,E,E,E,E},
			  										 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,B,W,E,E,E,E},
													 {E,E,B,W,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		test1States.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,E,W,E,E,E,E},
													 {E,E,W,W,W,E,E,E},
													 {E,E,B,B,W,E,E,E},
													 {E,E,W,B,W,E,E,E},
													 {E,E,E,E,B,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
			
		
		
		for(OthelloState state: test1States){
			System.out.println(state);
			assertTrue(test1List.contains(state));
		}
			
		// Checks that the Minimax PieceDifferential Player will pick the State with the most pieces gained/most enemy pieces lost
		boardGame1.move(blackChip);
		assertEquals(correctChoice1, boardGame1.getCurrentState()); // TODO: Incorrect here. Why?
				
	}

}
