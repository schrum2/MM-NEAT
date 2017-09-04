package edu.southwestern.tasks.rlglue.featureextractors.tetris;

import org.rlcommunity.environments.tetris.TetrisState;

/**
 * Utility class containing functions for RLGlue feature extractors
 * @author Lauren Gillespie
 *
 */
public class TetrisExtractorUtil {

	public static boolean isHole(int index, int[] worldState) {
		int x = getColumn(index);
		while(x < index) {
			if(worldState[x] == 1) { break;}
			x += TetrisState.worldWidth;
		}
		if(x == index) return false;
		while(x < index) {
			x += TetrisState.worldWidth;
		}
		return worldState[x] == 0;
	}

	/**
	 * Return x-coordinate corresponding to worldState index
	 * @param index in worldState (linear array)
	 * @return coresponding x-coordinate
	 */
	private static int getColumn(int index) {
		return index % TetrisState.worldWidth;
	}

	/**
	 * Calculate the linear array position from (x,y) components based on
	 * worldWidth. 
	 * 
	 * @param x x-coord
	 * @param y y-coord
	 * @return location in array
	 */
	public static int calculateLinearArrayPosition(int x, int y, int worldWidth) {
		int returnValue = y * worldWidth + x;
		//System.out.println("linear array index: " + returnValue);
		return returnValue;
	}
}
