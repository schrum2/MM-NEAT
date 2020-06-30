package edu.southwestern.tasks.loderunner.astar;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.southwestern.tasks.loderunner.LodeRunnerVGLCUtil;
import edu.southwestern.tasks.loderunner.astar.LodeRunnerState.LodeRunnerAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class LodeRunnerStateTest {
	
	public static final int LODE_RUNNER_LEVEL_1_ACTION_LENGTH = 94;
	public static final int LODE_RUNNER_LEVEL_2_ACTION_LENGTH = 117;

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLodeRunnerAStarPath() {
		//tests for level 1
		List<List<Integer>> level1 = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 1.txt"); //converts to JSON
		LodeRunnerState start1 = new LodeRunnerState(level1, false);
		Search<LodeRunnerAction,LodeRunnerState> search = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		@SuppressWarnings("unused")
		HashSet<LodeRunnerState> mostRecentVisited1 = null;
		ArrayList<LodeRunnerAction> actionSequence1 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			actionSequence1 = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).search(start1, true, 1000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited1 = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search).getVisited();
		
		//confirms action sequence is the correct length
		assertTrue(actionSequence1!=null);
		assertTrue(actionSequence1.size() == LODE_RUNNER_LEVEL_1_ACTION_LENGTH);
		
		//tests the action sequence for level 1
		Iterator<LodeRunnerAction> itr = actionSequence1.iterator();
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr.next());
		assertFalse(itr.hasNext());
		//end of testing actions for level 1
		
		//tests for level 2 
		List<List<Integer>> level2 = LodeRunnerVGLCUtil.convertLodeRunnerLevelFileVGLCtoListOfLevelForLodeRunnerState(LodeRunnerVGLCUtil.LODE_RUNNER_LEVEL_PATH+"Level 2.txt"); //converts to JSON
		LodeRunnerState start2 = new LodeRunnerState(level2, false);
		Search<LodeRunnerAction,LodeRunnerState> search2 = new AStarSearch<>(LodeRunnerState.manhattanToFarthestGold);
		@SuppressWarnings("unused")
		HashSet<LodeRunnerState> mostRecentVisited2 = null;
		ArrayList<LodeRunnerAction> actionSequence2 = null;
		try {
			//tries to find a solution path to solve the level, tries as many time as specified by the last int parameter 
			//represented by red x's in the visualization 
			actionSequence2 = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search2).search(start2, true, 1000000);
		} catch(Exception e) {
			System.out.println("failed search");
			e.printStackTrace();
		}
		//get all of the visited states, all of the x's are in this set but the white ones are not part of solution path 
		mostRecentVisited2 = ((AStarSearch<LodeRunnerAction, LodeRunnerState>) search2).getVisited();
		
		//confirms action sequence is the correct length
		assertTrue(actionSequence2!=null);
		assertTrue(actionSequence2.size()==LODE_RUNNER_LEVEL_2_ACTION_LENGTH);
		
		//tests of actions for level 2
		Iterator<LodeRunnerAction> itr2 = actionSequence2.iterator();
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.UP), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.RIGHT), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.DOWN), itr2.next());
		assertEquals(new LodeRunnerAction(LodeRunnerAction.MOVE.LEFT), itr2.next());
		assertFalse(itr2.hasNext());
		//end of test for level 2
	}

}
