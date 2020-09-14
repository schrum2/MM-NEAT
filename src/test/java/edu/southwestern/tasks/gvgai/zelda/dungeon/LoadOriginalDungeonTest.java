package edu.southwestern.tasks.gvgai.zelda.dungeon;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaLevelUtil;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaState.GridAction;
import edu.southwestern.util.search.AStarSearch;
import edu.southwestern.util.search.Search;

public class LoadOriginalDungeonTest {

	String[] toTest;
	
	@Before
	public void setUp() {
		Parameters.initializeParameterCollections(new String[] {"randomSeed:1"}); // All default parameters
		String format = "tloz%s_1_flip";
		int numberOfDungeons = 8;
		// Dungeon 9: Takes a long time for A* to run but it has never been finished before
		toTest = new String[numberOfDungeons];
		
		for(int i = 1; i <= numberOfDungeons; i++) {
			toTest[i - 1] = String.format(format, i);
		}
	}
	
	@Test
	public void test() {
		for(String dungeon : toTest) {
			System.out.println("Testing dungeon : " + dungeon);
			try {
				Dungeon d = LoadOriginalDungeon.loadOriginalDungeon(dungeon, false);
				
				ZeldaState initial = new ZeldaState(5, 5, 0, d);
				Search<GridAction,ZeldaState> search = new AStarSearch<>(ZeldaLevelUtil.manhattan);
				ArrayList<GridAction> result = search.search(initial);
				
				if(result == null)
					fail("Dungeon : " + dungeon + " did not complete the search");
				
			} catch (Exception e) {
				e.printStackTrace();
				fail("Error occured for dungeon : " + dungeon);
			}
		}
	}

}
