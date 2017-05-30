package boardGame.checkers;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CheckersStateTest {
	
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
		// TODO
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
		// TODO
	}

	@Test
	public void testMove() {
		fail("Not yet implemented");
		// TODO
	}

	@Test
	public void testCopy() {
		assertEquals(start, start.copy());
		// TODO
	}

	@Test
	public void testPossibleBoardGameStates() {
		fail("Not yet implemented");
		// TODO
	}

}
