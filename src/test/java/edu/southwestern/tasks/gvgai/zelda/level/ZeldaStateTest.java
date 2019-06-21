package edu.southwestern.tasks.gvgai.zelda.level;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Heuristic;
import edu.southwestern.util.search.Search;
import me.jakerg.rougelike.RougelikeApp;

public class ZeldaStateTest {
	
	public static final int ZELDA_ROOM_ROWS = 11; // This is actually the room height from the original game, since VGLC rotates rooms
	public static final int ZELDA_ROOM_COLUMNS = 16;
	
	@Test
	public void test() throws Exception {
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
				System.out.println("Calculating H for : " + s);
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
