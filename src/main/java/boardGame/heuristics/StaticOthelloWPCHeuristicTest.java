package boardGame.heuristics;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import boardGame.TwoDimensionalBoardGameState;
import boardGame.othello.OthelloState;

public class StaticOthelloWPCHeuristicTest {
	
//		{{ 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00}, // Weight Values for Othello WPC Heuristic
//		 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
//		 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
//		 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
//		 { 0.05, 0.01, 0.02, 0.01, 0.01, 0.02, 0.01, 0.05},
//		 { 0.10, 0.01, 0.05, 0.02, 0.02, 0.05, 0.01, 0.10},
//		 {-0.25,-0.25, 0.01, 0.01, 0.01, 0.01,-0.25,-0.25},
//		 { 1.00,-0.25, 0.10, 0.05, 0.05, 0.10,-0.25, 1.00}}
	
	
	// Only need to create the Heuristic once
	StaticOthelloWPCHeuristic bgh = new StaticOthelloWPCHeuristic();

	final static int E = TwoDimensionalBoardGameState.EMPTY;
	final static int B = OthelloState.BLACK_CHIP; // Player 1
	final static int W = OthelloState.WHITE_CHIP; // Player 2

	OthelloState start;
	OthelloState test1;
	OthelloState test2;
	OthelloState test3;
	
	@Before
	public void setUp() throws Exception {
		start = new OthelloState(); // Standard starting state
		
								//    0,1,2,3,4,5,6,7
		int[][] board1 = new int[][]{{E,E,E,E,E,E,E,E}, //  0
			 						 {E,E,B,B,B,B,B,E}, //  1
			 						 {E,E,B,B,B,B,B,E}, //  2
			 						 {E,E,B,B,W,B,B,E}, //  3
			 						 {E,E,B,B,B,B,B,E}, //  4
			 						 {E,E,B,B,B,B,B,E}, //  5
			 						 {E,E,E,E,E,E,E,E}, //  6
			 						 {E,E,E,E,E,E,E,E}};//  7
			 
			 test1 = new OthelloState(board1, W, new ArrayList<Integer>());

								//    0,1,2,3,4,5,6,7
	    int[][] board2 = new int[][]{{E,E,E,E,E,E,E,E}, //  0: Same as the above, but with the Players reversed; should be the same absolute value
	    							 {E,E,W,W,W,W,W,E}, //  1
	    							 {E,E,W,W,W,W,W,E}, //  2
	    							 {E,E,W,W,B,W,W,E}, //  3
	    							 {E,E,W,W,W,W,W,E}, //  4
	    							 {E,E,W,W,W,W,W,E}, //  5
	    							 {E,E,E,E,E,E,E,E}, //  6
	    							 {E,E,E,E,E,E,E,E}};//  7

	    	  test2 = new OthelloState(board2, B, new ArrayList<Integer>());
	    	  
	   int[][] board3 = new int[][]{{W,E,E,E,E,E,E,B}, //  0: Same as the above, but with the Players reversed; should be the same absolute value
	    							{E,W,E,E,E,E,B,E}, //  1
	  	    						{E,E,W,E,E,B,E,E}, //  2
	  	    						{E,E,E,W,B,E,E,E}, //  3
	  	    						{E,E,E,B,W,E,E,E}, //  4
	  	    						{E,E,B,E,E,W,E,E}, //  5
	  	    						{E,B,E,E,E,E,W,E}, //  6
	  	    						{B,E,E,E,E,E,E,W}};//  7

	  	    	test3 = new OthelloState(board3, B, new ArrayList<Integer>());
	}
	
	@Test
	public void testHeuristicEvalution() {
		// All evals are from Player 1's perspective; weights for Player 2 are negative
		assertEquals(0.0, bgh.heuristicEvalution(start), 0.0);
		// TODO: More Tests
		assertEquals(0.21, bgh.heuristicEvalution(test1), 0.00001);
		assertEquals(-0.21, bgh.heuristicEvalution(test2), 0.00001);
		assertEquals(0.0, bgh.heuristicEvalution(test3), 0.00001);
	}

}
