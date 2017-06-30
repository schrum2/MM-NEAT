package boardGame.connectFour;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class ConnectFourStateTest {
	
	private static final int B = ConnectFourState.BLACK_CHECK;
	private static final int R = ConnectFourState.RED_CHECK;
	private static final int E = ConnectFourState.EMPTY;
	
	private ConnectFourState start;
	private ConnectFourState test1;
	private ConnectFourState test2;
	private ConnectFourState test3;
	
	@Before
	public void setUp() throws Exception {
		start = new ConnectFourState();
		
		int[][] board1 = new int[][]{{E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E},
									 {E,E,E,E,E,E,E}};
		
		test1 = new ConnectFourState(board1, B, new ArrayList<Integer>());
	}

	@Test
	public void testSetupStartingBoard() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBoardWidth() {
		assertEquals(6, start.getBoardWidth());
	}

	@Test
	public void testGetBoardHeight() {
		assertEquals(7, start.getBoardHeight());
	}

	@Test
	public void testGetWinners() {
		assertEquals(new ArrayList<Integer>(), start.getWinners());
		
		// TODO: Test other states
	}

	@Test
	public void testGetPlayerSymbols() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayerColors() {
		fail("Not yet implemented");
	}

	@Test
	public void testMoveSinglePoint() {
		fail("Not yet implemented");
	}

	@Test
	public void testEndState() {
		fail("Not yet implemented");
	}

	@Test
	public void testPossibleBoardGameStates() {
		fail("Not yet implemented");
	}

	@Test
	public void testCopy() {
		fail("Not yet implemented");
	}

}
