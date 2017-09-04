package boardGame.ttt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import boardGame.BoardGameState;

public class TicTacToeStateTest {

	TicTacToeState start;
	TicTacToeState test1;
	TicTacToeState test2;
	TicTacToeState test3;
	TicTacToeState test4;
	
	public static final int X = TicTacToeState.X;
	public static final int O = TicTacToeState.O;
	public static final int E = TicTacToeState.EMPTY;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		start  = new TicTacToeState();
				
		int[][] board1 = new int[][]{ {X,E,E},
									  {E,O,E},
									  {X,E,E}
									};
		
		int[][] board2 = new int[][]{ {E,X,X},
									  {O,X,E},
									  {E,O,E}
									};
			
									
		int[][] board3 = new int[][]{ {X,O,X},
									  {O,X,O},
									  {X,O,X}
									};
										
									
		int[][] board4 = new int[][]{ {O,E,X},
									  {X,O,E},
									  {E,X,O}
									};
									
		test1 = new TicTacToeState(board1, O, new LinkedList<Integer>());
		
		test2 = new TicTacToeState(board2, O, new LinkedList<Integer>());
		
		LinkedList<Integer> win3 = new LinkedList<Integer>();
		win3.add(X);
		test3 = new TicTacToeState(board3, O, win3);
		
		LinkedList<Integer> win4 = new LinkedList<Integer>();
		win4.add(1);
		test4 = new TicTacToeState(board4, X, new LinkedList<Integer>());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetupStartingBoard() {
		
		start.setupStartingBoard(); // Does nothing to the empty Board; able to fill every Space
		
		assertTrue(start.moveSinglePoint(new Point(0,0)));	
		assertTrue(start.moveSinglePoint(new Point(0,1)));	
		assertTrue(start.moveSinglePoint(new Point(0,2)));	
		assertTrue(start.moveSinglePoint(new Point(1,0)));	
		assertTrue(start.moveSinglePoint(new Point(1,1)));	
		assertTrue(start.moveSinglePoint(new Point(1,2)));	
		assertTrue(start.moveSinglePoint(new Point(2,0)));	
		assertTrue(start.moveSinglePoint(new Point(2,1)));	
		assertTrue(start.moveSinglePoint(new Point(2,2)));	

		
		assertFalse(test3.moveSinglePoint(new Point(1,1))); // Unable to fill any more Spaces because the Board is full
		
		test3.setupStartingBoard(); // Does nothing to the Board; unable to fill any Spaces
		
		assertFalse(test3.moveSinglePoint(new Point(0,0)));	
		assertFalse(test3.moveSinglePoint(new Point(0,1)));	
		assertFalse(test3.moveSinglePoint(new Point(0,2)));
		assertFalse(start.moveSinglePoint(new Point(1,0)));	
		assertFalse(start.moveSinglePoint(new Point(1,1)));	
		assertFalse(start.moveSinglePoint(new Point(1,2)));	
		assertFalse(start.moveSinglePoint(new Point(2,0)));	
		assertFalse(start.moveSinglePoint(new Point(2,1)));	
		assertFalse(start.moveSinglePoint(new Point(2,2)));
	}

	@Test
	public void testGetBoardWidth() {
		assertEquals(3, start.getBoardWidth());
		assertEquals(3, test1.getBoardWidth());
		assertEquals(3, test2.getBoardWidth());
		assertEquals(3, test3.getBoardWidth());
		assertEquals(3, test4.getBoardWidth());
	}

	@Test
	public void testGetBoardHeight() {
		assertEquals(3, start.getBoardHeight());
		assertEquals(3, test1.getBoardHeight());
		assertEquals(3, test2.getBoardHeight());
		assertEquals(3, test3.getBoardHeight());
		assertEquals(3, test4.getBoardHeight());
	}

	@Test
	public void testGetPlayerSymbols() {
		assertArrayEquals(new char[]{'X','O'}, start.getPlayerSymbols());
	}

	@Test
	public void testGetPlayerColors() {
		assertArrayEquals(new Color[]{Color.blue, Color.red}, start.getPlayerColors());
	}

	@Test
	public void testFill() {
		assertTrue(start.moveSinglePoint(new Point(0,0)));	
		assertTrue(start.moveSinglePoint(new Point(0,1)));	
		assertTrue(start.moveSinglePoint(new Point(0,2)));	
		assertTrue(start.moveSinglePoint(new Point(1,0)));	
		assertTrue(start.moveSinglePoint(new Point(1,1)));	
		assertTrue(start.moveSinglePoint(new Point(1,2)));	
		assertTrue(start.moveSinglePoint(new Point(2,0)));	
		assertTrue(start.moveSinglePoint(new Point(2,1)));	
		assertTrue(start.moveSinglePoint(new Point(2,2)));	
		
		assertEquals(start, test3); // After filling Start with the above, it should resemble test3
		
		assertFalse(start.moveSinglePoint(new Point(1,1))); // Unable to fill any more Spaces because the Board is full
	}

	@Test
	public void testEndState() {
		assertFalse(start.endState());
		assertFalse(test1.endState());
		assertFalse(test2.endState());
		assertTrue(test3.endState());
		assertTrue(test4.endState());
	}

	@Test
	public void testCopy() {
		assertTrue(start.equals(start.copy()));
		assertTrue(test1.equals(test1.copy()));
		assertTrue(test2.equals(test2.copy()));
		assertTrue(test3.equals(test3.copy()));
		assertTrue(test4.equals(test4.copy()));
		
		assertFalse(start.equals(test1.copy()));
		assertFalse(test1.equals(test2.copy()));
		assertFalse(test2.equals(test3.copy()));
		assertFalse(test3.equals(test4.copy()));
		assertFalse(test4.equals(start.copy()));
	}

	@Test
	public void testPossibleBoardGameStates() {
		
		Set<BoardGameState> startList = start.possibleBoardGameStates(start);
		Set<BoardGameState> list1 = test1.possibleBoardGameStates(test1);
		Set<BoardGameState> list2 = test2.possibleBoardGameStates(test2);
		Set<BoardGameState> list3 = test3.possibleBoardGameStates(test3);
		Set<BoardGameState> list4 = test4.possibleBoardGameStates(test4);
		
		
		
		Set<BoardGameState> testStart = new HashSet<BoardGameState>();

		testStart.add(new TicTacToeState(new int[][]{{X,E,E}, {E,E,E}, {E,E,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,X,E}, {E,E,E}, {E,E,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,E,X}, {E,E,E}, {E,E,E}}, O, new ArrayList<Integer>()));
		
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {X,E,E}, {E,E,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {E,X,E}, {E,E,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {E,E,X}, {E,E,E}}, O, new ArrayList<Integer>()));
		
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {E,E,E}, {X,E,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {E,E,E}, {E,X,E}}, O, new ArrayList<Integer>()));
		testStart.add(new TicTacToeState(new int[][]{{E,E,E}, {E,E,E}, {E,E,X}}, O, new ArrayList<Integer>()));
		

		Set<BoardGameState> testList1 = new HashSet<BoardGameState>();

		testList1.add(new TicTacToeState(new int[][]{{X,O,E}, {E,O,E}, {X,E,E}}, X, new ArrayList<Integer>()));
		testList1.add(new TicTacToeState(new int[][]{{X,E,O}, {E,O,E}, {X,E,E}}, X, new ArrayList<Integer>()));
		testList1.add(new TicTacToeState(new int[][]{{X,E,E}, {O,O,E}, {X,E,E}}, X, new ArrayList<Integer>()));

		testList1.add(new TicTacToeState(new int[][]{{X,E,E}, {E,O,O}, {X,E,E}}, X, new ArrayList<Integer>()));
		testList1.add(new TicTacToeState(new int[][]{{X,E,E}, {E,O,E}, {X,O,E}}, X, new ArrayList<Integer>()));
		testList1.add(new TicTacToeState(new int[][]{{X,E,E}, {E,O,E}, {X,E,O}}, X, new ArrayList<Integer>()));

		
		Set<BoardGameState> testList2 = new HashSet<BoardGameState>();
		
		testList2.add(new TicTacToeState(new int[][]{{O,X,X},{O,X,E},{E,O,E}}, X, new ArrayList<Integer>()));
		testList2.add(new TicTacToeState(new int[][]{{E,X,X},{O,X,O},{E,O,E}}, X, new ArrayList<Integer>()));
		testList2.add(new TicTacToeState(new int[][]{{E,X,X},{O,X,E},{O,O,E}}, X, new ArrayList<Integer>()));
		testList2.add(new TicTacToeState(new int[][]{{E,X,X},{O,X,E},{E,O,O}}, X, new ArrayList<Integer>()));

		
		Set<BoardGameState> testList3 = new HashSet<BoardGameState>(); // test3 is an EndState that's completely full; this list remains empty
		
		
		Set<BoardGameState> testList4 = new HashSet<BoardGameState>(); // test4 is an EndState that's partially empty; still has possible States
		
		List<Integer> win4 = new ArrayList<Integer>();
		win4.add(O);
		testList4.add(new TicTacToeState(new int[][]{{O,X,X},{X,O,E},{E,X,O}}, O, win4));
		testList4.add(new TicTacToeState(new int[][]{{O,E,X},{X,O,X},{E,X,O}}, O, win4));
		testList4.add(new TicTacToeState(new int[][]{{O,E,X},{X,O,E},{X,X,O}}, O, win4));
		

		for(BoardGameState tic : startList){
			assertTrue(testStart.contains(tic));
		}
		
		
		for(BoardGameState tic : list1){
			assertTrue(testList1.contains(tic));
		}
		
		
		for(BoardGameState tic : list2){
			assertTrue(testList2.contains(tic));
		}
		
		
		for(BoardGameState tic : list3){
			assertTrue(testList3.contains(tic));
		}
		
		for(BoardGameState tic : list4){
			assertTrue(testList4.contains(tic));	
		}
		
	}

}
