package boardGame.checkers;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CheckersStateTest {
	
	private  final int BK = CheckersState.BLACK_CHECK_KING;
	private  final int RK = CheckersState.RED_CHECK_KING;
	private  final int B = CheckersState.BLACK_CHECK;
	private  final int R = CheckersState.RED_CHECK;
	private  final int E = CheckersState.EMPTY;
	
	CheckersState start;
	CheckersState test1;
	CheckersState test2;
	CheckersState test3;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		start = new CheckersState();
		
		// Technically Unused, but useful. DO NOT DELETE
		@SuppressWarnings("unused")
		int[][] boardStart = new int[][]{{E,B,E,B,E,B,E,B}, // 0
										 {B,E,B,E,B,E,B,E}, // 1
										 {E,B,E,B,E,B,E,B}, // 2
										 {E,E,E,E,E,E,E,E}, // 3
										 {E,E,E,E,E,E,E,E}, // 4
										 {R,E,R,E,R,E,R,E}, // 5
										 {E,R,E,R,E,R,E,R}, // 6
										 {R,E,R,E,R,E,R,E}};// 7
									 
		int[][] board1 = new int[][]{{E,E,E,E ,E ,E,E,E}, // 0 // Test King Movement
									 {E,E,E,E ,E ,E,E,E}, // 1
									 {E,E,E,BK,E ,E,E,E}, // 2
									 {E,E,E,E ,E ,E,E,E}, // 3
									 {E,E,E,E ,E ,E,E,E}, // 4
									 {E,E,E,E ,RK,E,E,E}, // 5
									 {E,E,E,E ,E ,E,E,E}, // 6
									 {E,E,E,E ,E ,E,E,E}};// 7
									 
		test1 = new CheckersState(board1, 0, new ArrayList<Integer>());
		
		
		int[][] board2 = new int[][]{{E,B,E,B,E,B,E,B}, // 0 Test Double Jumping/ Multi-Jumping
									 {B,E,B,E,B,E,B,E}, // 1
									 {E,B,E,B,E,B,E,B}, // 2
									 {E,E,E,E,E,E,E,E}, // 3
									 {E,E,E,E,E,E,E,E}, // 4
									 {R,E,R,E,R,E,R,E}, // 5
									 {E,R,E,R,E,R,E,R}, // 6
									 {R,E,R,E,R,E,R,E}};// 7
			 
		test2 = new CheckersState(board2, 0, new ArrayList<Integer>());
		
		
		int[][] board3 = new int[][]{{E,E,E,E,E,E,E,E}, // 0 Improbable, but good for Testing End State
									 {E,E,E,E,E,E,E,E}, // 1
									 {E,E,E,E,E,E,E,E}, // 2
									 {E,E,E,E,E,E,E,E}, // 3
									 {E,E,E,E,E,E,E,E}, // 4
									 {R,E,R,E,R,E,R,E}, // 5
									 {E,E,E,E,E,E,E,E}, // 6
									 {E,E,E,E,E,E,E,E}};// 7

		List<Integer> win3 = new ArrayList<Integer>();
		win3.add(1);
		
		test3 = new CheckersState(board3, 1, new ArrayList<Integer>());
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupStartingBoard() {
		fail("Not yet implemented");
		// TODO
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
		assertEquals(new ArrayList<Integer>(), start.getWinners());
		assertEquals(new ArrayList<Integer>(), test1.getWinners()); // Not an End State; no Winners yet
		assertEquals(new ArrayList<Integer>(), test2.getWinners()); // Not an End State; no Winners yet
		
		List<Integer> win3 = new ArrayList<Integer>();
		win3.add(1);
		
		assertEquals(win3, test3.getWinners()); // End State; Player 2 wins
	}

	@Test
	public void testGetPlayerSymbols() {
		assertArrayEquals(new char[]{'B', 'R'}, start.getPlayerSymbols());
	}

	@Test
	public void testGetPlayerColors() {
		assertArrayEquals(new Color[]{Color.black, Color.black, Color.red, Color.red}, start.getPlayerColors());
	}

	@Test
	public void testEndState() {
		assertFalse(start.endState());
		assertFalse(test1.endState());
		assertFalse(test2.endState());
		assertTrue(test3.endState());
	}

	@Test
	public void testMove() {
		assertTrue(start.move(new Point(2,1), new Point(3,0))); // Known good Starting move
		assertTrue(start.move(new Point(5,0), new Point(4,1))); // Known good Enemy Starting move
		assertFalse(start.move(new Point(2,3), new Point(3,3))); // Tried to move Non-Diagonally
		assertFalse(start.move(new Point(1,4), new Point(2,5))); // Tried to move to a Non-Empty, Non-Enemy Space
		
		// TODO: King Movement, Jumping, Double Jumping
	}

	@Test
	public void testCopy() {
		assertEquals(start, start.copy());
		assertEquals(test1, test1.copy());
		assertEquals(test2, test2.copy());
		assertEquals(test3, test3.copy());
	}

	@Test
	public void testPossibleBoardGameStates() {
		Set<CheckersState> startSet = start.possibleBoardGameStates(start);
		
		Set<CheckersState> startTest = new HashSet<CheckersState>();
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
			 										{B,E,B,E,B,E,B,E}, // 1
			 										{E,E,E,B,E,B,E,B}, // 2
			 										{B,E,E,E,E,E,E,E}, // 3
			 										{E,E,E,E,E,E,E,E}, // 4
			 										{R,E,R,E,R,E,R,E}, // 5
			 										{E,R,E,R,E,R,E,R}, // 6
			 										{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,E,E,B,E,B,E,B}, // 2
													{E,E,B,E,E,E,E,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,B,E,E,E,B,E,B}, // 2
													{E,E,B,E,E,E,E,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,B,E,E,E,B,E,B}, // 2
													{E,E,E,E,B,E,E,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
	
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,B,E,B,E,E,E,B}, // 2
													{E,E,E,E,B,E,E,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
			
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,B,E,B,E,E,E,B}, // 2
													{E,E,E,E,E,E,B,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, // 0
													{B,E,B,E,B,E,B,E}, // 1
													{E,B,E,B,E,B,E,E}, // 2
													{E,E,E,E,E,E,B,E}, // 3
													{E,E,E,E,E,E,E,E}, // 4
													{R,E,R,E,R,E,R,E}, // 5
													{E,R,E,R,E,R,E,R}, // 6
													{R,E,R,E,R,E,R,E}} , 1, new ArrayList<Integer>()));
		
		// TODO
		
		for(CheckersState state: startSet){
			assertTrue(startTest.contains(state));
		}
		
		
		
	}

}
