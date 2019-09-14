package edu.southwestern.tasks.gvgai.zelda.level;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ZeldaLevelUtilTest {

	@Test
	public void findMaxDistanceOfLevelTest() {
		int[][] level1 = {{1, 1, 1, 0, 0, 0},
						  {1, 1, 1, 1, 1, 0},
						  {1, 1, 1, 1, 1, 0},
						  {1, 1, 1, 1, 1, 0},
						  {1, 1, 1, 1, 1, 0}};
		
		
		assertEquals(ZeldaLevelUtil.findMaxDistanceOfLevel(level1, 3, 0), 6);
		
		int[][] level2 = {{0, 0},
				          {0, 0}};
		
		assertEquals(ZeldaLevelUtil.findMaxDistanceOfLevel(level2, 0, 0), 2);
		
		int[][] level3 = {{0, 0, 0, 0, 0},
						  {0, 1, 1, 1, 0},
						  {0, 1, 0, 1, 0},
						  {0 ,1, 0, 0, 0},
						  {0, 1, 1, 1, 0},
						  {0, 0, 0, 0, 0}};
		
		assertEquals(ZeldaLevelUtil.findMaxDistanceOfLevel(level3, 2, 2), 12);
		assertEquals(ZeldaLevelUtil.findMaxDistanceOfLevel(level3, 0, 0), 10);
		assertEquals(ZeldaLevelUtil.findMaxDistanceOfLevel(level3, 2, 3), 11);
	}

}
