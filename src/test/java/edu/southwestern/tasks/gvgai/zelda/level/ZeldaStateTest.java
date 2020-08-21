package edu.southwestern.tasks.gvgai.zelda.level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;

public class ZeldaStateTest {
	
	public static final int ZELDA_ROOM_ROWS = 11; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int ZELDA_ROOM_COLUMNS = 16;
	
	@Test
	public void test() throws Exception {
		// This unusual setting to 2 indicates that this is an older test. P is now mapped to 5
		Parameters.initializeParameterCollections(new String[] {"zeldaVGLCWaterPMapCode:2"}); 
		Dungeon dungeon = LoadOriginalDungeon.loadOriginalDungeon("a_test", false);
		
		Heuristic<GridAction,ZeldaState> manhattan = new Heuristic<GridAction,ZeldaState>() {

			@Override
			public double h(ZeldaState s) {
				Dungeon d = s.getDungeon();
				Point goalPoint = d.getCoords(d.getGoal());
				int gDX = goalPoint.x;
				int gDY = goalPoint.y;
				
				Point g = d.getGoalPoint();
				int gX = g.x;
				int gY = g.y;
				int i = Math.abs(s.x - gX) + Math.abs(s.y - gY);
				int j = Math.abs(gDX - s.dX) * ZELDA_ROOM_COLUMNS + Math.abs(gDY - s.dY) * ZELDA_ROOM_ROWS;
				return i + j; 
			}
		};
		
		ZeldaState initial = new ZeldaState(5, 5, 0, dungeon);
		
		Search<GridAction,ZeldaState> search = new AStarSearch<>(manhattan);
		ArrayList<GridAction> result = search.search(initial);
		
		if(result != null)
			for(GridAction a : result)
				System.out.println(a.getD().toString());
		
		Iterator<GridAction> itr = result.iterator();
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertFalse(itr.hasNext());
		
		Dungeon dungeon1 = LoadOriginalDungeon.loadOriginalDungeon("a_test_1", false);
		ZeldaState initial1 = new ZeldaState(5, 5, 0, dungeon1);
		Search<GridAction,ZeldaState> search1 = new AStarSearch<>(manhattan);
		ArrayList<GridAction> result1 = search1.search(initial1);
		
//		HashSet<ZeldaState> mostRecentVisited = ((AStarSearch<GridAction, ZeldaState>) search1).getVisited();
//		HashSet<ZeldaState> solutionPath = new HashSet<>();
//		ZeldaState currentState = initial1;
//		solutionPath.add(currentState);
//		for(GridAction a : result1) {
//			currentState = (ZeldaState) currentState.getSuccessor(a);
//			solutionPath.add(currentState);
//		}
//		BufferedImage image = DungeonUtil.viewDungeon(dungeon1, mostRecentVisited, solutionPath);
//		MiscUtil.waitForReadStringAndEnterKeyPress();
		
		itr = result1.iterator();
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertFalse(itr.hasNext());
		
		Dungeon dungeon2 = LoadOriginalDungeon.loadOriginalDungeon("a_test_2", false);
		ZeldaState initial2 = new ZeldaState(5, 5, 0, dungeon2);
		Search<GridAction,ZeldaState> search2 = new AStarSearch<>(manhattan);
		ArrayList<GridAction> result2 = search2.search(initial2);
		
		itr = result2.iterator();
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.LEFT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.DOWN), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.RIGHT), itr.next());
		assertEquals(new GridAction(GridAction.DIRECTION.UP), itr.next());
		assertFalse(itr.hasNext());
	}

}
