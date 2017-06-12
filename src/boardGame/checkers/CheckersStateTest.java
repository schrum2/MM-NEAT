package boardGame.checkers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
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
	CheckersState test4;
	CheckersState test5;
	CheckersState test6; // Used to test the incorrect End State Error

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
									 
		int[][] board1 = new int[][]{{E ,E ,E ,E ,E ,E ,E ,E}, // 0 // Test King Movement
									 {E ,E ,E ,E ,E ,E ,E ,E}, // 1
									 {E ,E ,E ,BK,E ,E ,E ,E}, // 2
									 {E ,E ,E ,E ,E ,E ,E ,E}, // 3
									 {E ,E ,E ,E ,E ,E ,E ,E}, // 4
									 {E ,E ,E ,E ,RK,E ,E ,E}, // 5
									 {E ,E ,E ,E ,E ,E ,E ,E}, // 6
									 {E ,E ,E ,E ,E ,E ,E ,E}};// 7
									 
		test1 = new CheckersState(board1, 0, new ArrayList<Integer>());
		
		
		int[][] board2 = new int[][]{{E,E,E,E,E,E,E,E}, // 0 Test Single Jumping/Forced Jump
									 {E,E,E,E,E,E,E,E}, // 1
									 {E,E,E,B,E,E,E,E}, // 2
									 {E,E,E,E,R,E,E,E}, // 3
									 {E,E,E,E,E,E,E,E}, // 4
									 {E,E,E,E,E,E,E,E}, // 5
									 {E,E,E,E,E,E,E,E}, // 6
									 {E,E,E,E,E,E,E,E}};// 7
			 
		test2 = new CheckersState(board2, 0, new ArrayList<Integer>());
		
		
		int[][] board3 = new int[][]{{E,E,E,E,E,E,E,E}, // 0 Improbable, but good for Testing End State and getWinners()
									 {E,E,E,E,E,E,E,E}, // 1
									 {E,E,E,E,E,E,E,E}, // 2
									 {E,E,E,E,E,E,E,E}, // 3
									 {E,E,E,E,E,E,E,E}, // 4
									 {R,E,R,E,R,E,R,E}, // 5
									 {E,E,E,E,E,E,E,E}, // 6
									 {E,E,E,E,E,E,E,E}};// 7
		
		test3 = new CheckersState(board3, 1, new ArrayList<Integer>());
		
		int[][] board4 = new int[][]{{E,E,E,E,E,E,E,B}, // 0 Double/Multi-Jump Testing
			 						 {E,E,E,E,E,E,E,E}, // 1
			 						 {E,E,E,B,E,E,E,E}, // 2
			 						 {E,E,R,E,E,E,E,E}, // 3
			 						 {E,E,E,E,E,E,E,E}, // 4
			 						 {E,E,R,E,E,E,E,E}, // 5
			 						 {E,E,E,E,E,E,E,E}, // 6
			 						 {E,E,E,E,E,E,E,E}};// 7
		
		test4 = new CheckersState(board4, 0, new ArrayList<Integer>());
		
		int[][] board5 = new int[][]{{E,E,E,E,E,E,E,E},
			 						 {E,E,E,E,E,E,E,E},
			 						 {E,E,E,E,E,E,E,E},
			 						 {E,E,R,E,E,E,E,E},
			 						 {E,E,E,E,E,E,E,E},
			 						 {E,E,R,E,E,E,E,E},
			 						 {E,E,E,E,E,B,E,E},
			 						 {E,E,E,E,E,E,E,E}};

		test5 = new CheckersState(board5, 0, new ArrayList<Integer>());
		
		int[][] board6 = new int[][]{{E,B,E,B,E,B,E,B}, // 0 Used to test the End State Error
									 {B,E,B,E,B,E,B,E}, // 1
									 {E,B,E,B,E,R,E,E}, // 2
									 {E,E,E,E,E,E,B,E}, // 3
									 {E,E,E,E,E,E,E,E}, // 4
									 {R,E,E,E,R,E,R,E}, // 5
									 {E,R,E,R,E,R,E,R}, // 6
									 {R,E,R,E,R,E,R,E}};// 7

		test6 = new CheckersState(board6, 1, new ArrayList<Integer>());  // Used to test the incorrect EndState Error
	}

	@Test
	public void testSetupStartingBoard() {
		assertFalse(test1.moveDoublePoint(new Point(2,1), new Point(3,2))); // No Check at that Point
		test1.setupStartingBoard(); // Resets the entire Board
		assertEquals(start, test1); // test1 now is a Starting State
		assertTrue(test1.moveDoublePoint(new Point(2,1), new Point(3,2))); // Known good Starting Move
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
		assertArrayEquals(new char[]{'b', 'r', 'B', 'R'}, start.getPlayerSymbols());
	}

	@Test
	public void testGetPlayerColors() {
		assertArrayEquals(new Color[]{Color.black, Color.red, new Color(64,64,64), new Color(255,81,81)}, start.getPlayerColors());
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
		// Basic Movement

		assertTrue(start.moveDoublePoint(new Point(2,1), new Point(3,2))); // Known good Starting move
		assertTrue(start.moveDoublePoint(new Point(5,0), new Point(4,1))); // Known good Enemy Starting move
		assertFalse(start.moveDoublePoint(new Point(2,3), new Point(3,3))); // Tried to move Non-Diagonally
		assertFalse(start.moveDoublePoint(new Point(1,4), new Point(2,5))); // Tried to move to a Non-Empty, Non-Enemy Space
		
		assertFalse(start.moveDoublePoint(new Point(3,2), new Point(2,1))); // Tried to move a Black Non-King Check up
		assertFalse(start.moveDoublePoint(new Point(2,3), new Point(3,4))); // Tried to make a Non-Jump Move when a Jump is available; Forced Jump
		assertTrue(start.moveDoublePoint(new Point(3,2), new Point(4,1))); // Known good Move; needed to change Players
		assertFalse(start.moveDoublePoint(new Point(4,1), new Point(5,0))); // Tried to move a Red Non-King Check down		
		
		
		// King Movement
		assertTrue(test1.moveDoublePoint(new Point(2,3), new Point(3,2))); // Black King Down-Left
		assertTrue(test1.moveDoublePoint(new Point(5,4), new Point(6,3))); // Red King Down-Left
		
		assertTrue(test1.moveDoublePoint(new Point(3,2), new Point(2,3))); // Black King Up-Right
		assertTrue(test1.moveDoublePoint(new Point(6,3), new Point(5,4))); // Red King Up-Right
		
		assertTrue(test1.moveDoublePoint(new Point(2,3), new Point(1,2))); // Black King Up-Left
		assertTrue(test1.moveDoublePoint(new Point(5,4), new Point(4,3))); // Red King Up-Left
		
		assertTrue(test1.moveDoublePoint(new Point(1,2), new Point(2,3))); // Black King Down-Right
		assertTrue(test1.moveDoublePoint(new Point(4,3), new Point(5,4))); // Red King Down-Right
		
		assertTrue(test4.moveDoublePoint(new Point(2,3), new Point(3,2))); // Made the First Jump; must now make the Second Jump
		assertFalse(test4.moveDoublePoint(new Point(5,2), new Point(4,1))); // Tried to Move the Enemy Check
		assertFalse(test4.moveDoublePoint(new Point(0,7), new Point(1,6))); // Tried to Move the Non-Double Jump Check
		assertTrue(test4.moveDoublePoint(new Point(4,1), new Point(5,2))); // Made the Second Jump
		
		assertTrue(test5.moveDoublePoint(new Point(6,5), new Point(7,6))); // Moves a Black Check to the end of the Board
		assertTrue(test5.moveDoublePoint(new Point(3,2), new Point(2,3))); // Moves an Enemy Check; needed to change Players
		assertTrue(test5.moveDoublePoint(new Point(7,6), new Point(6,5))); // Able to move the new Black King Check back up
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

		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,E,E,B,E,B,E,B}, 
													{B,E,E,E,E,E,E,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,E,E,B,E,B,E,B}, 
													{E,E,B,E,E,E,E,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,B,E,E,E,B,E,B}, 
													{E,E,B,E,E,E,E,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,B,E,E,E,B,E,B}, 
													{E,E,E,E,B,E,E,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
			
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,B,E,B,E,E,E,B}, 
													{E,E,E,E,B,E,E,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,B,E,B,E,E,E,B}, 
													{E,E,E,E,E,E,B,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		startTest.add(new CheckersState(new int[][]{{E,B,E,B,E,B,E,B}, 
													{B,E,B,E,B,E,B,E}, 
													{E,B,E,B,E,B,E,E}, 
													{E,E,E,E,E,E,B,E}, 
													{E,E,E,E,E,E,E,E}, 
													{R,E,R,E,R,E,R,E}, 
													{E,R,E,R,E,R,E,R}, 
													{R,E,R,E,R,E,R,E}}, 1, new ArrayList<Integer>()));
		
		
		for(CheckersState state : startSet){
			assertTrue(startTest.contains(state));
		}

	Set<CheckersState> test1Set = test1.possibleBoardGameStates(test1);
		
		Set<CheckersState> test1Test = new HashSet<CheckersState>();

		test1Test.add(new CheckersState(new int[][]{{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,BK,E ,E ,E ,E ,E},
													{E ,E ,E ,E, E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,RK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E}}, 1, new ArrayList<Integer>()));
		
		test1Test.add(new CheckersState(new int[][]{{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,BK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,RK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E}}, 1, new ArrayList<Integer>()));
		
		test1Test.add(new CheckersState(new int[][]{{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,BK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,RK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E}}, 1, new ArrayList<Integer>()));
		
		test1Test.add(new CheckersState(new int[][]{{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,BK,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,RK,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E},
													{E ,E ,E ,E ,E ,E ,E ,E}}, 1, new ArrayList<Integer>()));
		
		for(CheckersState state : test1Set){
			assertTrue(test1Test.contains(state));
		}
		
		
		Set<CheckersState> test2Set = test2.possibleBoardGameStates(test2);
		
		Set<CheckersState> test2Test = new HashSet<CheckersState>();
		
		test2Test.add(new CheckersState(new int[][]{{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,B,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		for(CheckersState state : test2Set){
			assertTrue(test2Test.contains(state));
		}
		
		Set<CheckersState> test4Set = test4.possibleBoardGameStates(test4);
		
		Set<CheckersState> test4ATest = new HashSet<CheckersState>();
		
		test4ATest.add(new CheckersState(new int[][]{{E,E,E,E,E,E,E,B},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,B,E,E,E,E,E,E},
													 {E,E,R,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 0, new ArrayList<Integer>()));
		for(CheckersState state : test4Set){
			assertTrue(test4ATest.contains(state));
		}
		test4.moveDoublePoint(new Point(2,3), new Point(3,2));
		test4Set = test4.possibleBoardGameStates(test4);
		
		Set<CheckersState> test4BTest = new HashSet<CheckersState>();
		
		test4BTest.add(new CheckersState(new int[][]{{E,E,E,E,E,E,E,B},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,E,E,E,E,E},
													 {E,E,E,B,E,E,E,E},
													 {E,E,E,E,E,E,E,E}}, 1, new ArrayList<Integer>()));
		
		for(CheckersState state : test4BTest){
			assertTrue(test4Set.contains(state));
		}
		
		
		
		Set<CheckersState> test5Set = test5.possibleBoardGameStates(test5);
		
		Set<CheckersState> test5Test = new HashSet<CheckersState>();
		
		test5Test.add(new CheckersState(new int[][]{{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,R,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,R,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,BK,E}}, 1, new ArrayList<Integer>()));
		
		test5Test.add(new CheckersState(new int[][]{{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,R,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,R,E,E,E,E,E},
													{E,E,E,E,E,E,E,E},
													{E,E,E,E,BK,E,E,E}}, 1, new ArrayList<Integer>()));
		
		for(CheckersState state : test5Set){
			assertTrue(test5Test.contains(state));
		}
		
//		Set<CheckersState> test6Set = test6.possibleBoardGameStates(test6);
//		
//		System.out.println("Size: " + test6Set.size() + " End: " + test6.endState());
//		System.out.println("Winners: " + test6.getWinners());
	}

}
