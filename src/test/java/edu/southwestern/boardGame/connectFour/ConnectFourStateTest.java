package edu.southwestern.boardGame.connectFour;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class ConnectFourStateTest {
	
	private static final int B = ConnectFourState.BLACK_CHECK;
	private static final int R = ConnectFourState.RED_CHECK;
	private static final int E = ConnectFourState.EMPTY;
	
	private ConnectFourState start;
	private ConnectFourState test1;
	private ConnectFourState test2;

	
	@Before
	public void setUp() throws Exception {
		start = new ConnectFourState();
		
		int[][] board1 = new int[][]{{E,E,E,E,E,B},
									 {E,E,E,E,E,B},
									 {E,E,E,E,E,E},
									 {E,E,E,E,E,R},
									 {E,E,E,E,E,R},
									 {E,E,E,E,E,R},
									 {E,E,E,E,B,R}};
		
		
		
		test1 = new ConnectFourState(board1, B, new ArrayList<Integer>());
		
		int[][] board2 = new int[][]{{B,R,B,R,B,R},
			 						 {R,B,R,B,R,B},
			 						 {B,R,B,R,B,R},
			 						 {R,B,R,B,R,B},
			 						 {B,R,B,R,B,R},
			 						 {R,B,R,B,R,B},
			 						 {B,R,B,R,B,R}};
			 						
		
		test2 = new ConnectFourState(board2, R, new ArrayList<Integer>());
	}

	@Test
	public void testSetupStartingBoard() {
		assertFalse(test2.moveSinglePoint(new Point(6,0)));
		test2.setupStartingBoard();
		assertTrue(test2.moveSinglePoint(new Point(6,0)));
	}

	@Test
	public void testGetBoardWidth() {
		assertEquals(7, start.getBoardWidth());
	}

	@Test
	public void testGetBoardHeight() {
		assertEquals(6, start.getBoardHeight());
	}

	@Test
	public void testGetWinners() {
		fail("Not Yet Implemented");
		// TODO: This test takes a long time for some reason; find and fix the cause
//		assertEquals(new ArrayList<Integer>(), start.getWinners());
//		
//		List<Integer> win1 = new ArrayList<Integer>();
//		win1.add(R);
//		assertEquals(win1, test1.getWinners());
//		
//		List<Integer> win2 = new ArrayList<Integer>();
//		win2.add(R);
//		win2.add(B);
//		assertEquals(win2, test2.getWinners());
	}

	@Test
	public void testGetPlayerSymbols() {
		assertArrayEquals(new char[]{'R', 'B'}, start.getPlayerSymbols());
	}

	@Test
	public void testGetPlayerColors() {
		assertArrayEquals(new Color[]{Color.red, Color.black}, start.getPlayerColors());
	}

	@Test
	public void testMoveSinglePoint() {
		// Able to fill up every point in Start
		for(int i = 0; i < start.getBoardWidth(); i++){
			for(int j = 0; j < start.getBoardHeight(); j++){
				assertTrue(start.moveSinglePoint(new Point(i,0)));
			}
		}
		
		// Now unable to play on any Space on the Board
		for(int i = 0; i < start.getBoardWidth(); i++){
			assertFalse(start.moveSinglePoint(new Point(i,0)));
		}
	}

	@Test
	public void testEndState() {
		fail("Not Yet Implemented");
		// TODO: This test takes a long time for some reason; find and fix the cause
//		assertFalse(start.endState());
//		assertTrue(test1.endState());
//		assertTrue(test2.endState());
	}

	@Test
	public void testPossibleBoardGameStates() {
		Set<ConnectFourState> startSet = start.possibleBoardGameStates(start);
		List<ConnectFourState> startList = new ArrayList<ConnectFourState>();
		for(int i = 0; i < start.getBoardWidth(); i++){
			ConnectFourState temp = start.copy();
			temp.moveSinglePoint(new Point(i, 0));
			startList.add(temp);
		}
		
		for(ConnectFourState bgs : startSet){
			assertTrue(startList.contains(bgs));
		}
		// TODO: Possibly add more States here?
	}

	@Test
	public void testCopy() {
		assertEquals(start, start.copy());
		assertEquals(test1, test1.copy());
		assertEquals(test2, test2.copy());
	}

}
