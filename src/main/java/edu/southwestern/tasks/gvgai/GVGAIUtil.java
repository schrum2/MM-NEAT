package edu.southwestern.tasks.gvgai;

import java.util.Arrays;

import edu.southwestern.networks.Network;
import edu.southwestern.util.graphics.GraphicsUtil;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * 
 * Utility class for GVG-AI games. Methods shared by all.
 * 
 * @author Jacob Schrum
 *
 */
public class GVGAIUtil {

	public static final double PRESENCE_THRESHOLD = 0.0;
	public static final int PRESENCE_INDEX = 0;
	public static final int FIRST_FIXED_INDEX = 1;
	public static final double RANDOM_ITEM_THRESHOLD = 0.3;
	
	/**
	 * Take a CPPN and some sprite information to create a level
	 * for a grid-based game.
	 * 
	 * @param n The Neural network CPPN
	 * @param levelWidth Width in grid units
	 * @param levelHeight Height in grid units
	 * @param defaultBackground char that corresponds to nothing/floor/background
	 * @param border char that surrounds level as a well (TODO: not all games have this)
	 * @param fixed array of chars for fixed items, like walls, etc.
	 * @param unique array of chars for items/sprites of which there must be exactly one
	 * @param random array of chars for items/sprites of which there can be a variable/random number
	 * @param randomItems Number of items from the random array to place
	 * @return String array with level layout
	 */
	public static String[] generateLevelFromCPPN(Network n, double[] inputMultiples, int levelWidth, int levelHeight, 
			char defaultBackground, char border, char[] fixed, char[] unique, char[] random, int randomItems, char[] bottomItems) {
		// Start with 2D char array to fill out level: The +2 is for the border wall.
		char[][] level = new char[levelHeight+2][levelWidth+2];
		// Background
		for(int i = 0; i < level.length; i++) {
			Arrays.fill(level[i], defaultBackground);
		}
		// Border wall: TODO: Does not apply to all games ... remove?
		for(int y = 0; y < levelHeight+2; y++) { // Vertical walls
			level[y][0] = border;
			level[y][levelWidth+1] = border;
		}		
		for(int x = 1; x < levelWidth+1; x++) { // Horizontal walls
			level[0][x] = border;
			level[levelHeight+1][x] = border;
		}
		// Query CPPN
		double[] uniqueScores = new double[unique.length];
		// Location with highest score will have the unique item
		Arrays.fill(uniqueScores, Double.NEGATIVE_INFINITY);
		int[][] uniqueLocations = new int[unique.length][2];
		// Query spots within the border
		for(int y = 1; y < levelHeight+1; y++) {
			for(int x = 1; x < levelWidth+1; x++) {
				// Able to use a method from GraphicsUtil here. The -1 is time, which is ignored.
				double[] inputs = GraphicsUtil.get2DObjectCPPNInputs(x, y, levelWidth, levelHeight, -1);
				// Multiplies the inputs by the inputMultiples; used to turn on or off the effects in each input
				for(int i = 0; i < inputMultiples.length; i++) {
					inputs[i] = inputs[i] * inputMultiples[i];
				}
				double[] outputs = n.process(inputs);
				// Check if a fixed item is present
				if(outputs[PRESENCE_INDEX] > PRESENCE_THRESHOLD) {
					// Figure out which one it is
					double[] fixedActivations = new double[fixed.length];
					System.arraycopy(outputs, FIRST_FIXED_INDEX, fixedActivations, 0, fixed.length);
					int whichFixed = StatisticsUtilities.argmax(fixedActivations);
					level[y][x] = fixed[whichFixed]; // Place item in level
				}
				// Only place unique items on empty spaces
				if(level[y][x] == defaultBackground) {
					// Find maximal output for each unique item
					for(int i = 0; i < unique.length; i++) {
						// Store maximal location queried for each unique item
						if(outputs[i+fixed.length] > uniqueScores[i] && unclaimed(x,y,uniqueLocations)) {
							uniqueScores[i] = outputs[i+fixed.length];
							uniqueLocations[i] = new int[]{x,y};
						}
					}		
					// Now place random items: unique items have priority, random items are limited
					if(randomItems > 0) {
						//System.out.println("random check " + outputs[fixed.length+unique.length]);
						// Last CPPN output
						if(outputs[fixed.length+unique.length] > RANDOM_ITEM_THRESHOLD) {
							// Select one of the random item options
							level[y][x] = random[RandomNumbers.randomGenerator.nextInt(random.length)];
							randomItems--; // allow one fewer random item
						}
					}
				}
			}
		}
		// Place the unique items
		for(int i = 0; i < unique.length; i++) {
			level[uniqueLocations[i][1]][uniqueLocations[i][0]] = unique[i];
		}		
		
		// TODO: Add game-specific hacks here
		// TODO: For example, for aliens, put the ship near the bottom
		
		
		// Convert to String array
		String[] stringLevel = new String[levelHeight+2];
		for(int i = 0; i < level.length; i++) {
			stringLevel[i] = new String(level[i]);
		}
		return stringLevel;
	}
	
	/**
	 * Make sure that no unique item is currently claiming the spot (x,y)
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param uniqueLocations Current claimed unique item locations
	 * @return Whether the location is free
	 */
	private static boolean unclaimed(int x, int y, int[][] uniqueLocations) {
		for(int i = 0; i < uniqueLocations.length; i++) {
			if(uniqueLocations[i][0] == x && uniqueLocations[i][1] == y) {
				return false;
			}
		}
		return true;
	}
}
