package boardGame.othello;

import static org.junit.Assert.*;

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

import boardGame.BoardGameState;

public class OthelloStateTest {
	
	OthelloState start;
	OthelloState test1;
	
	private final int e = OthelloState.EMPTY;
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
		int[][] startBoard = new int[][]{{e,e,e,e,e,e,e,e}, //  0
										 {e,e,e,e,e,e,e,e}, //  1
										 {e,e,e,e,e,e,e,e}, //  2
										 {e,e,e,B,W,e,e,e}, //  3
										 {e,e,e,W,B,e,e,e}, //  4
										 {e,e,e,e,e,e,e,e}, //  5
										 {e,e,e,e,e,e,e,e}, //  6
										 {e,e,e,e,e,e,e,e}};// 7

		start = new OthelloState(startBoard, B, new ArrayList<Integer>());
		
		
								//    0,1,2,3,4,5,6,7
		int[][] board1 = new int[][]{{e,e,e,e,e,e,e,e}, //  0: Technically impossible (or at least improbable), but allows for easier testing
									 {e,e,B,B,B,B,B,e}, //  1
									 {e,e,B,B,B,B,B,e}, //  2
									 {e,e,B,B,W,B,B,e}, //  3
									 {e,e,B,B,B,B,B,e}, //  4
									 {e,e,B,B,B,B,B,e}, //  5
									 {e,e,e,e,e,e,e,e}, //  6
									 {e,e,e,e,e,e,e,e}};// 7
									 
		test1 = new OthelloState(board1, W, new ArrayList<Integer>());
									 
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupStartingBoard() {
		start.setupStartingBoard();
		assertEquals(new OthelloState(), start);
		assertTrue(start.move(new Point(3,5))); // Still able to make this starting Move
		
		test1.setupStartingBoard(); // Doesn't actually clear the Board; should be unable to make the starting Move
		assertFalse(test1.move(new Point(3,5)));
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
	}

	@Test
	public void testMove() {
		assertTrue(start.move(new Point(3, 5))); // Known Point that can be played at the start of Othello
		assertFalse(start.move(new Point(3, 4))); // Enemy Chip at specified Point
		
		// Test all 8 possible Directional Moves; copies the original State to avoid changing Players
		assertTrue(((OthelloState) test1.copy()).move(new Point(6,4))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(0,4))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(3,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(3,7))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(6,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(6,7))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(0,1))); // Possible Move
		assertTrue(((OthelloState) test1.copy()).move(new Point(0,7))); // Possible Move
		
		assertFalse(((OthelloState) test1.copy()).move(new Point(5,6))); // Enemy Chip at specified Point
		assertFalse(((OthelloState) test1.copy()).move(new Point(4,1))); // Non-Linear Path
		assertFalse(((OthelloState) test1.copy()).move(new Point(1,3))); // Enemy at Point and Non-Linear Path
	}

	@Test
	public void testPossibleBoardGameStates() {
		
		Set<BoardGameState> listStart = start.possibleBoardGameStates(start);
		Set<BoardGameState> list1 = test1.possibleBoardGameStates(test1);
		
		
		
		Set<BoardGameState> listTestStart = new HashSet<BoardGameState>();
		listTestStart.add(new OthelloState(new int[][]{{e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,B,B,B,e,e},
													   {e,e,e,W,B,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e}}, W, new ArrayList<Integer>()));

		listTestStart.add(new OthelloState(new int[][]{{e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,B,e,e,e},
													   {e,e,e,B,B,e,e,e},
													   {e,e,e,W,B,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e}}, W, new ArrayList<Integer>()));
		
		listTestStart.add(new OthelloState(new int[][]{{e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,B,W,e,e,e},
													   {e,e,e,B,B,e,e,e},
													   {e,e,e,B,e,e,e,e},
													   {e,e,e,e,e,e,e,e},
													   {e,e,e,e,e,e,e,e}}, W, new ArrayList<Integer>()));
		
		listTestStart.add(new OthelloState(new int[][]{{e,e,e,e,e,e,e,e},
			   										   {e,e,e,e,e,e,e,e},
			   										   {e,e,e,e,e,e,e,e},
			   										   {e,e,e,B,W,e,e,e},
			   										   {e,e,B,B,B,e,e,e},
			   										   {e,e,e,e,e,e,e,e},
			   										   {e,e,e,e,e,e,e,e},
			   										   {e,e,e,e,e,e,e,e}}, W, new ArrayList<Integer>()));
		
		
		
		
		Set<BoardGameState> listTest1 = test1.possibleBoardGameStates(test1);
		
		OthelloState testBoard1 = (OthelloState) test1.copy();
		testBoard1.move(new Point(6,4));
		listTest1.add(testBoard1);
		
		OthelloState testBoard2 = (OthelloState) test1.copy();
		testBoard2.move(new Point(0,4));
		listTest1.add(testBoard2);
		
		OthelloState testBoard3 = (OthelloState) test1.copy();
		testBoard3.move(new Point(3,1));
		listTest1.add(testBoard3);

		OthelloState testBoard4 = (OthelloState) test1.copy();
		testBoard4.move(new Point(3,7));
		listTest1.add(testBoard4);

		OthelloState testBoard5 = (OthelloState) test1.copy();
		testBoard5.move(new Point(6,1));
		listTest1.add(testBoard5);

		OthelloState testBoard6 = (OthelloState) test1.copy();
		testBoard6.move(new Point(6,7));
		listTest1.add(testBoard6);

		OthelloState testBoard7 = (OthelloState) test1.copy();
		testBoard7.move(new Point(0,1));
		listTest1.add(testBoard7);

		OthelloState testBoard8 = (OthelloState) test1.copy();
		testBoard8.move(new Point(0,7));
		listTest1.add(testBoard8);
		
		
		
		for(BoardGameState othello: listStart){
			System.out.println(othello);
			assertTrue(listTestStart.contains(othello)); // TODO: Still doesn't work for some reason; all possible BoardStates are in the test.
		}
		
		for(BoardGameState othello: list1){
			assertTrue(listTest1.contains(othello));
		}
	}

	@Test
	public void testCopy() {
		assertEquals(start, start.copy());
		assertEquals(test1, test1.copy());
	}

}
