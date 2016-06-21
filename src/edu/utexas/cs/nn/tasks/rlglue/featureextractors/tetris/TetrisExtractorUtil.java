package edu.utexas.cs.nn.tasks.rlglue.featureextractors.tetris;

import org.rlcommunity.environments.tetris.TetrisState;

/**
 * Utility class containing functions for RLGlue feature extractors
 * @author Lauren Gillespie
 *
 */
public class TetrisExtractorUtil {

	public static boolean isHole(int index, int[] worldState) {
		int x = getColumn(index);
		boolean isBlockAbove = false;
		while(x < index) {
			if(worldState[x] == 1) { isBlockAbove = true;}
			x += TetrisState.worldWidth;
		}
		if(worldState[index] == 1 && isBlockAbove) { return true;}
		// Loop while empty (x += width): ignore open space at top
		//	if x >= index return false
		
		// Loop to bottom:
		//  if x == width return empty ? true : false
		
		// Should never get outside of loop! Error message
		
		// TODO Auto-generated method stub
		return false;
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
