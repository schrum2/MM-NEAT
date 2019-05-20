package edu.southwestern.tasks.gvgai.zelda.level;

import java.util.LinkedList;
import java.util.List;

import edu.southwestern.util.datastructures.Triple;

/**
 * 
 * @author Jake Gutierrez
 *
 */
public class ZeldaLevelUtil {
	/**
	 * Find the longest shortest path distance given a 2D array and start points
	 * @param level 2D int array representing the level, passable = 0
	 * @param startX Where to start on the x axis
	 * @param startY Where to start on the y axis
	 * @return int longest shortest distance
	 */
	public static int findMaxDistanceOfLevel(int[][] level, int startX, int startY) {
		// List of all the points we have visited included distance
		LinkedList<Triple<Integer, Integer, Integer>> visited = new LinkedList<>();
		int[][] dist = new int[level.length][level[0].length]; // Keep track of where we have visited in relation to x, y coordiantes
		int max = 0; // Our max so far
		
		// Push the initial point, startX and startY with a distance of 0
		visited.push(new Triple<Integer, Integer, Integer>(startX, startY, 0));
		
		// Initialize our dist array with -1, where we haven't visited
		for(int i = 0; i < dist.length; i++)
			for(int j = 0; j < dist[i].length; j++)
				dist[i][j] = -1;
		
		// While we have points to visit
		while(visited.size() != 0) {
			Triple<Integer, Integer, Integer> point = visited.remove(0); // Get the top of the list
			int currentX = point.t1;
			int currentY = point.t2;
			int d = point.t3;
			
			// Set this to visited marking the distance
			dist[currentY][currentX] = d;
			max = Math.max(max, d); // Set max
			
			// Check points horizontally and vertically and add them if they pass the test
			checkPointToAdd(level, dist, visited, currentX + 1, currentY, d + 1);
			checkPointToAdd(level, dist, visited, currentX, currentY + 1, d + 1);
			checkPointToAdd(level, dist, visited, currentX - 1, currentY, d + 1);
			checkPointToAdd(level, dist, visited, currentX, currentY - 1, d + 1);
		}
		
		return max;
	}

	/**
	 * Figure out if we need to add the given point or not if it's not out of bounds
	 * if it hasn't been visited, and if it's not already in the visited list
	 * @param level 2D representation of the level
	 * @param dist 2D array of where we have visited
	 * @param visited List of all points w/ distances that have been visited so far
	 * @param x point to check on x
	 * @param y point to check on y
	 * @param d distance to be added
	 */
	private static void checkPointToAdd(int[][] level, int[][] dist,
			LinkedList<Triple<Integer, Integer, Integer>> visited, int x, int y, int d) {
		
		// Out of bounds check
		if(x < 0 || x >= level[0].length || y < 0 || y >= level.length) return;

		// If haven't been visited check
		if(dist[y][x] != -1) return;
		
		// If the point is possible
		if(level[y][x] != 0) return;
		
		// loop through visited, and return early if the x,y coordinates are present
		for(Triple<Integer, Integer, Integer> point : visited)
			if(point.t1 == x && point.t2 == y) return;
		
		// Finally add point
		visited.add(new Triple<Integer, Integer, Integer>(x, y, d));
		
	}

	/**
	 * Helper function to convert 2D list of ints to 2d array of ints
	 * @param level 2D list representation of given level
	 * @return 2D int array of level
	 */
	public static int[][] listToArray(List<List<Integer>> level) {
		int[][] lev = new int[level.size()][level.get(0).size()];
		for(int i = 0; i < lev.length; i++)
			for(int j = 0; j < lev[i].length; j++)
				lev[i][j] = level.get(i).get(j);
		
		return lev;
	}
}
