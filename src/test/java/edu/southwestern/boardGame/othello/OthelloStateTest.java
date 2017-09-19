package edu.southwestern.boardGame.othello;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.boardGame.BoardGameState;
import edu.southwestern.parameters.Parameters;

public class OthelloStateTest {
	
	OthelloState start;
	OthelloState test1;
	OthelloState test2;
	
	
	private final int E = OthelloState.EMPTY;
	private final int B = OthelloState.BLACK_CHIP;
	private final int W = OthelloState.WHITE_CHIP;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Parameters.initializeParameterCollections(new String[]{"io:false", "netio:false", "task:edu.southwestern.tasks.boardGame.StaticOpponentBoardGameTask",
				"boardGame:edu.southwestern.boardGame.othello.Othello", "minimaxSearchDepth:3", 
				"randomArgMaxTieBreak:false"});
		MMNEAT.loadClasses();
		
		int[][] startBoard = new int[][]{{E,E,E,E,E,E,E,E}, //  0
										 {E,E,E,E,E,E,E,E}, //  1
										 {E,E,E,E,E,E,E,E}, //  2
										 {E,E,E,B,W,E,E,E}, //  3
										 {E,E,E,W,B,E,E,E}, //  4
										 {E,E,E,E,E,E,E,E}, //  5
										 {E,E,E,E,E,E,E,E}, //  6
										 {E,E,E,E,E,E,E,E}};// 7

		start = new OthelloState(startBoard, B, new ArrayList<Integer>());
		
		
								//    0,1,2,3,4,5,6,7
		int[][] board1 = new int[][]{{E,E,E,E,E,E,E,E}, //  0: Technically impossible (or at least improbable), but allows for easier testing
									 {E,E,B,B,B,B,B,E}, //  1
									 {E,E,B,B,B,B,B,E}, //  2
									 {E,E,B,B,W,B,B,E}, //  3
									 {E,E,B,B,B,B,B,E}, //  4
									 {E,E,B,B,B,B,B,E}, //  5
									 {E,E,E,E,E,E,E,E}, //  6
									 {E,E,E,E,E,E,E,E}};// 7
									 
		test1 = new OthelloState(board1, W, new ArrayList<Integer>());
		
		
								//    0,1,2,3,4,5,6,7
		int[][] board2 = new int[][]{{B,B,B,B,B,B,B,B}, //  0: Testing the State as documented in issue #408
									 {B,B,B,B,B,B,B,W}, //  1
									 {B,B,B,W,W,B,W,W}, //  2
									 {B,B,B,B,B,E,E,E}, //  3
									 {B,B,W,W,W,W,W,E}, //  4
									 {B,E,W,W,E,W,W,W}, //  5
									 {E,E,W,E,E,E,E,E}, //  6
									 {E,E,E,E,E,E,E,E}};// 7
									 
		test2 = new OthelloState(board2, W, new ArrayList<Integer>());
	}	

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupStartingBoard() {
		start.setupStartingBoard();
		assertEquals(new OthelloState(), start);
		assertTrue(start.moveSinglePoint(new Point(3,5))); // Still able to make this starting Move
		
		test1.setupStartingBoard(); // Doesn't actually clear the Board; should be unable to make the starting Move
		assertFalse(test1.moveSinglePoint(new Point(3,5)));
	}

	@Test
	public void testGetBoardWidth() {
		assertEquals(8, start.getBoardWidth());
	}

	@Test
	public void testGetBoardHeight() {
		assertEquals(8, start.getBoardHeight());
	}

	@Test
	public void testGetWinners() {
		assertEquals(new ArrayList<Integer>(), start.getWinners()); // Start is not an EndState; empty winners list
		assertEquals(new ArrayList<Integer>(), test1.getWinners()); // test1 is not an EndState; empty winners list
	
		// Play a game between WPC and random. The WPC should win with extremely high probability.
//		Othello game = new Othello();
//		@SuppressWarnings("unchecked")
//		BoardGamePlayer<OthelloState>[] players = new BoardGamePlayer[]{new BoardGamePlayerMinimaxAlphaBetaPruning<OthelloState>(new StaticOthelloWPCHeuristic()), new BoardGamePlayerRandom<OthelloState>()};
//
//		BoardGameFitnessFunction<OthelloState> fitness = new SimpleWinLoseDrawBoardGameFitness<OthelloState>();
//		ArrayList<BoardGameFitnessFunction<OthelloState>> fitnesses = new ArrayList<>();
//		fitnesses.add(fitness);
		
		// Make WPC beat random 10 times
		// PROBLEM: Can the WPC really not beat random 20 times in a row?
//		for(int i = 0; i < 10; i++) {
//			game.reset();
//			//Parameters.parameters.setBoolean("printFitness", true);
//			ArrayList<Pair<double[], double[]>> result = BoardGameUtil.playGame(game, players, fitnesses, new ArrayList<>());
//			assertEquals(1, result.get(0).t1[0], 0.0); // A value of 1 for this fitness means the WPC won as both the first and second player
//			//Parameters.parameters.setBoolean("printFitness", false);		
//		}
	}

	@Test
	public void testGetPlayerSymbols() {
		assertArrayEquals(new char[]{'B', 'W'}, start.getPlayerSymbols());
	}

	@Test
	public void testGetPlayerColors() {
		assertArrayEquals(new Color[]{Color.black, Color.white}, start.getPlayerColors());
	}

	@Test
	public void testEndState() {
		assertFalse(start.endState());
		assertFalse(test1.endState());
		assertFalse(test2.endState());
	}

	@Test
	public void testMove() {
		assertTrue(start.moveSinglePoint(new Point(3, 5))); // Known Point that can be played at the start of Othello
		assertFalse(start.moveSinglePoint(new Point(3, 4))); // Enemy Chip at specified Point
		
		// Test all 8 possible Directional Moves; copies the original State to avoid changing Players
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(6,4))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(0,4))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(3,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(3,7))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(6,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(6,7))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(0,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).moveSinglePoint(new Point(0,7))); // Possible Move
		
		assertFalse(((OthelloState) test1.copy()).moveSinglePoint(new Point(5,6))); // Enemy Chip at specified Point
		assertFalse(((OthelloState) test1.copy()).moveSinglePoint(new Point(4,1))); // Non-Linear Path
		assertFalse(((OthelloState) test1.copy()).moveSinglePoint(new Point(1,3))); // Enemy at Point and Non-Linear Path
	}

	@Test
	public void testPossibleBoardGameStates() {
		
		Set<OthelloState> listStart = start.possibleBoardGameStates(start);
		Set<OthelloState> list1 = test1.possibleBoardGameStates(test1);
		
		
		
		Set<BoardGameState> listTestStart = new HashSet<BoardGameState>();
		listTestStart.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,B,B,B,E,E},
													   {E,E,E,W,B,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E}}, W, new ArrayList<Integer>()));

		listTestStart.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,B,E,E,E},
													   {E,E,E,B,B,E,E,E},
													   {E,E,E,W,B,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E}}, W, new ArrayList<Integer>()));
		
		listTestStart.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,B,W,E,E,E},
													   {E,E,E,B,B,E,E,E},
													   {E,E,E,B,E,E,E,E},
													   {E,E,E,E,E,E,E,E},
													   {E,E,E,E,E,E,E,E}}, W, new ArrayList<Integer>()));
		
		listTestStart.add(new OthelloState(new int[][]{{E,E,E,E,E,E,E,E},
			   										   {E,E,E,E,E,E,E,E},
			   										   {E,E,E,E,E,E,E,E},
			   										   {E,E,E,B,W,E,E,E},
			   										   {E,E,B,B,B,E,E,E},
			   										   {E,E,E,E,E,E,E,E},
			   										   {E,E,E,E,E,E,E,E},
			   										   {E,E,E,E,E,E,E,E}}, W, new ArrayList<Integer>()));
		
		
		
		
		Set<BoardGameState> listTest1 = new HashSet<BoardGameState>();
		
		OthelloState testBoard1 = (OthelloState) test1.copy();
		testBoard1.moveSinglePoint(new Point(6,4));
		listTest1.add(testBoard1);
		
		OthelloState testBoard2 = (OthelloState) test1.copy();
		testBoard2.moveSinglePoint(new Point(0,4));
		listTest1.add(testBoard2);
		
		OthelloState testBoard3 = (OthelloState) test1.copy();
		testBoard3.moveSinglePoint(new Point(3,1));
		listTest1.add(testBoard3);

		OthelloState testBoard4 = (OthelloState) test1.copy();
		testBoard4.moveSinglePoint(new Point(3,7));
		listTest1.add(testBoard4);

		OthelloState testBoard5 = (OthelloState) test1.copy();
		testBoard5.moveSinglePoint(new Point(6,1));
		listTest1.add(testBoard5);

		OthelloState testBoard6 = (OthelloState) test1.copy();
		testBoard6.moveSinglePoint(new Point(6,7));
		listTest1.add(testBoard6);

		OthelloState testBoard7 = (OthelloState) test1.copy();
		testBoard7.moveSinglePoint(new Point(0,1));
		listTest1.add(testBoard7);

		OthelloState testBoard8 = (OthelloState) test1.copy();
		testBoard8.moveSinglePoint(new Point(0,7));
		listTest1.add(testBoard8);
		
		
		
		for(BoardGameState othello: listStart){
			assertTrue(listTestStart.contains(othello));
		}
		
		for(BoardGameState othello: list1){
			assertTrue(listTest1.contains(othello));
		}
		
		
		
		Set<OthelloState> list2 = test2.possibleBoardGameStates(test2);
		Set<OthelloState> listTest2 = new HashSet<OthelloState>();
		listTest2.add(test2.pass()); // W can't make a Move; needs to Pass. The pass() method creates a copy representing a successful Pass.
		
		OthelloState temp = null;
		
		for(OthelloState othello: list2){ // List2 has a size of 1; can only pass. Should not be an End State, however.
			assertTrue(listTest2.contains(othello));
			assertFalse(othello.endState());
			temp = othello; // Only one State in the Set
		}
		
		list2 = temp.possibleBoardGameStates(temp); // Should now be B's turn
		
		assertTrue(list2.size() > 1);
	}

	@Test
	public void testPass(){
		OthelloState start = new OthelloState();
		
		// Start of Game
		assertTrue(start.numPasses == 0 && start.getCurrentPlayer() == 0 && !start.endState()); // 0 Passes, Player 1's Turn, Is an End State
		start = start.pass();
		assertTrue(start.numPasses == 1 && start.getCurrentPlayer() == 1 && !start.endState()); // 1 Passes, Player 2's Turn, Is an End State
		start = start.pass();
		assertTrue(start.numPasses == 2 && start.getCurrentPlayer() == 0 && start.endState()); // 2 Passes, Player 1's Turn, Is an End State
	}
	
	@Test
	public void testCopy() {
		assertEquals(start, start.copy());
		assertEquals(test1, test1.copy());
	}

}
