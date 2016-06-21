package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;
/**
 * Utility class containing functions for RLGlue feature extractors
 * @author Lauren Gillespie
 *
 */
public class TetrisExtractorUtil {


	/**
	 * Calculate the linear array position from (x,y) components based on
	 * worldWidth. Package level access so we can use it in tests.
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public static int calculateLinearArrayPosition(int x, int y, int worldWidth) {
		int returnValue = y * worldWidth + x;
		return returnValue;
	}

	/**
	 * Extracts number of holes(empty space with a block above it) in input space
	 * @return number of holes
	 */
	public static int calculateHoles(int worldHeight, int worldWidth, int[] worldState)  {
		int holes = 0;
		for(int i = 0; i < worldWidth; i++) {
			for(int j = 0; j < worldHeight; j++) {
				holes += columnHoles(i, worldState, columnHeight(i ,worldState, worldHeight), worldHeight);
			}
		}
		
		return holes;
	}
	
public static int[] setHoles(int worldHeight, int worldWidth, int[] worldState) {
	for(int i = 0 ; i < worldWidth; i++) {
		isHole(i, worldState, columnHeight(i, worldState, worldHeight), worldHeight);
	}
	return worldState;
}
	/**
	 * Finds the number of holes in a given column for the worldstate
	 * @param x
	 * @param intArray
	 * @param height
	 * @return holes in a given column
	 */
	public static void isHole(int x, int[] worldState, int height, int worldHeight) {
		for (int y = worldHeight - height; y < worldHeight; y++) {
			if (worldState[calculateLinearArrayPosition(x, y, worldHeight)] == 0) {
				worldState[calculateLinearArrayPosition(x, y, worldHeight)] = -1;
			}
		}
	}
	
	/**
	 * Finds the number of holes in a given column for the worldstate
	 * @param x
	 * @param intArray
	 * @param height
	 * @return holes in a given column
	 */
	public static int columnHoles(int x, int[] worldState, int height, int worldHeight) {
		int holes = 0;
		for (int y = worldHeight - height; y < worldHeight; y++) {
			if (worldState[calculateLinearArrayPosition(x, y, worldHeight)] == 0) {
				holes++;
			}
		}
		return holes;
	}
	
	/**
	 * Finds the height of a column based on the current row and worldstate
	 * @param x
	 * @param intArray
	 * @return world height
	 */
	public static int columnHeight(int x, int[] intArray, int worldHeight) {
		int y = 0;
		while (y < worldHeight && intArray[TetrisExtractorUtil.calculateLinearArrayPosition(x, y, worldHeight)] == 0) {
			y++;
		}
		return worldHeight - y;
	}
}
