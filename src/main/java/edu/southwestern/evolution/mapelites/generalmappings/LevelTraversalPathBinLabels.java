package edu.southwestern.evolution.mapelites.generalmappings;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.southwestern.evolution.mapelites.BaseBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.mario.gan.GANProcess;
import edu.southwestern.util.util2D.ILocated2D;

/**
 * This class is a new binning scheme that can apply to any game where
 * an A* agent charts a path through the level. To avoid having too many
 * bins, a level is discretized at a coarser level (where it can be parameterizable).
 * The paths are stored as bit strings with 0's to represent rooms that will not
 * be used and 1's to represent rooms that are necessary to complete the dungeon.
 * 
 * @author Maxx Batterton
 *
 */
public class LevelTraversalPathBinLabels extends BaseBinLabels {
	
	private List<String> labels = null;
	
	private HashMap<String,Integer> labelToIndex;
	
	private int verticalCoarseness; // height of a level
	private int horizontalCoarseness; // width of a level
	private int sizeX; // width of a room
	private int sizeY; // height of a room
	private int maxSize; // used so the value of verticalCoarseness * horizontalCoarseness is not repeated multiple times.
	private int numOfBins; // used so the value of Math.pow(2, veriticalCoarseness * horizontalCoarseness) is not repeated multiple times.
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			switch(GANProcess.type) {
				case MARIO:
					throw new UnsupportedOperationException("Mario does not work with LevelTraversalPathBinLabels.");
				case ZELDA:
					this.verticalCoarseness = Parameters.parameters.integerParameter("zeldaGANLevelHeightChunks");
					this.horizontalCoarseness = Parameters.parameters.integerParameter("zeldaGANLevelWidthChunks");
					this.sizeX = 16; // Magic numbers for the size of a zelda room
					this.sizeY = 11; 
					this.maxSize = verticalCoarseness * horizontalCoarseness;
					this.numOfBins = (int) Math.pow(2, maxSize); 
					break;
				case MEGA_MAN:
					throw new UnsupportedOperationException("MegaMan does not work with LevelTraversalPathBinLabels.");
				case LODE_RUNNER:
					throw new UnsupportedOperationException("Lode Runner does not work with LevelTraversalPathBinLabels.");
				default:
					throw new UnsupportedOperationException("Pick a game");
			}
			labels = new ArrayList<String>(); // Starts small and will grow in capacity as needed
			labelToIndex = new HashMap<>();  // Will be small and grow in capacity as needed
			generateBinLabels(maxSize, "");
			((ArrayList<String>) labels).trimToSize(); // eliminate unused space in array
			System.out.println("Number of LevelTraversalPathBinLabels: "+ labels.size());
		}
		return labels;
	}
	
	private void generateBinLabels(int maxSize, String previous) {
		for (int i = 0; i < numOfBins; i++) {
			String binString = padStringTo(maxSize, Integer.toBinaryString(i));
			if(binStringConnected(binString)) { // Check the string and only add it if it is connected
				labelToIndex.put(binString, labels.size()); // Size is the next index filled
				labels.add(binString);
			}
		}
	}
	
	private boolean binStringConnected(String binString) {
		return binStringConnected(binString,verticalCoarseness,horizontalCoarseness);
	}
	
	public static boolean binStringConnected(String binString, int verticalCoarseness, int horizontalCoarseness) {
		// fill 2d array to then search
		boolean[][] boolDungeon = new boolean[verticalCoarseness][horizontalCoarseness];
		Point startingPoint = new Point();
		boolean startingPointFound = false;
		int binStringCounter = 0;
		
		for(int i = 0; i < verticalCoarseness; i++) { // go through rows in one level before going down vertically
			for(int j = 0; j < horizontalCoarseness; j++) {
				if(binString.charAt(binStringCounter) == '1') { // fill booleanDungeon with true at [i][j]
					if(!startingPointFound) {
						startingPointFound = true; // starting point is first location where value of "1" is found
						startingPoint.setLocation(i, j);
					}
					boolDungeon[i][j] = true;
				} else { // fill booleanDungeon with false at [i][j]
					boolDungeon[i][j] = false;
				}
				binStringCounter++; // binStringCounter increases so the binString advances
			}
		}
		explore(boolDungeon, startingPoint,verticalCoarseness,horizontalCoarseness); // explore connected rooms
		boolean result = isConnected(boolDungeon,verticalCoarseness,horizontalCoarseness); 
		assert !binString.equals("1000100000000000") || result : "1000100000000000 should be connected: "+startingPoint+" in "+Arrays.deepToString(boolDungeon);
		return result;
	}

	// dungeon is disconnected if any true's are found since all connected ones from starting point were explored and changed to false
	private static boolean isConnected(boolean[][] boolDungeon, int verticalCoarseness, int horizontalCoarseness) {
		boolean connected = true; // assuming connected unless otherwise 
		for(int i = 0; connected && i < verticalCoarseness; i++) { // go through rows in one level before going down vertically
			for(int j = 0; connected && j < horizontalCoarseness; j++) {
				if(boolDungeon[i][j]) connected = false; // disconnected because true exists in the array after connected rooms were explored
			}
		}
		return connected;
	}

	// kick-off method for recursive explore method
	private static void explore(boolean[][] boolDungeon, Point startingPoint, int verticalCoarseness, int horizontalCoarseness) {
		int x = startingPoint.x;
		int y = startingPoint.y;
		explore(boolDungeon, x, y,verticalCoarseness,horizontalCoarseness); 
	}

	/**
	 * This method recursively explores connected rooms when they exist
	 * in bounds. Locations where boolDungeon[x][y] are true will be explored.
	 * 
	 * @param boolDungeon 2D Array of boolean values where true represents something to explore and false otherwise
	 * @param x Integer representation of horizontal positioning
	 * @param y Integer representation of vertical positioning
	 * @param verticalCoarseness Integer vertical coarseness of the 2D array
	 * @param horizontalCoarseness Integer horizontal coarseness of the 2D array
	 */
	private static void explore(boolean[][] boolDungeon, int x, int y, int verticalCoarseness, int horizontalCoarseness) { 
		if(boolDungeon[x][y]) { // true, which is the same as having 1, explore this point's adjacent tiles
			boolDungeon[x][y] = false; // change current location to false since it has just been explored
			if(inBounds(x, y - 1,verticalCoarseness,horizontalCoarseness)) explore(boolDungeon, x, y - 1,verticalCoarseness,horizontalCoarseness); // explore upper adjacent if it exists
		    if(inBounds(x, y + 1,verticalCoarseness,horizontalCoarseness)) explore(boolDungeon, x, y + 1,verticalCoarseness,horizontalCoarseness); // explore lower adjacent if it exists
	 		if(inBounds(x - 1, y,verticalCoarseness,horizontalCoarseness)) explore(boolDungeon, x - 1, y,verticalCoarseness,horizontalCoarseness); // explore left adjacent if it exists
			if(inBounds(x + 1, y,verticalCoarseness,horizontalCoarseness)) explore(boolDungeon, x + 1, y,verticalCoarseness,horizontalCoarseness); // explore right adjacent if it exists
		} 
	}
	
	// checks if a position in an array is out of bounds either horizontally or vertically
	private static boolean inBounds(int firstIndex, int secondIndex, int verticalCoarseness, int horizontalCoarseness) {
		return firstIndex >= 0 && firstIndex < horizontalCoarseness 
				&& secondIndex >= 0 && secondIndex < verticalCoarseness; // x and y out of bounds if this returns false
	}

	// pads string with 0s when necessary
	private String padStringTo(int length, String s) {
		while (s.length() < length) s = "0" + s;
		
		return s;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		return multi[0];
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		String binKey = getBitStringLabel(keys);
		
		//System.out.println(binKey);
		
		assert labelToIndex.containsKey(binKey) : "Does not contain "+binKey+" in "+horizontalCoarseness+" by "+verticalCoarseness+" dungeon. HashMap:"+labelToIndex;
		// Use HashMap to find index in bins
		int binIndex = labelToIndex.get(binKey);
		return new int[] {binIndex};
		
	}

	@SuppressWarnings("unchecked")
	public String getBitStringLabel(HashMap<String, Object> keys) {
		// Create the path from the Level Path that is reported
		HashSet<ILocated2D> path = new HashSet<>();
		if (keys.containsKey("Level Path"))	path = (HashSet<ILocated2D>) keys.get("Level Path");
		
		// Map each room to an index in the visited tiles
		boolean[][] visitedTiles = new boolean[verticalCoarseness][horizontalCoarseness];
		int tileX;
		int tileY;
		// Traverse path and mark bins if visited
		for (ILocated2D location : path) {
			tileX = (int) location.getX() / sizeX;
			tileY = (int) location.getY() / sizeY;
			visitedTiles[tileY][tileX] = true;
		}
		
		//System.out.println(Arrays.deepToString(visitedTiles));
		
		// Old approach that assumes we have a bin for every binary number, and could thus convert binary to decimal to find the index.
		// Does not work if we do not maintain superfluous bins.
		// based on what bins were visited, assemble a bit string
//		int locationSum = 0;
//		for (int y = 0; y < verticalCoarseness; y++) {
//			for (int x = 0; x < horizontalCoarseness; x++) {
//				if (visitedTiles[verticalCoarseness - 1 - x][horizontalCoarseness - 1 - y]) {
//					locationSum += Math.pow(2d, x + verticalCoarseness * y);
//				}
//			}
//		}
//		// then return the bitstring as the the single bin for this scheme
//		return new int[] {locationSum};
		
		// Compute String of the binary sequence
		String binKey = "";
		for (int y = 0; y < verticalCoarseness; y++) {
			for (int x = 0; x < horizontalCoarseness; x++) {
				if (visitedTiles[y][x]) {
					binKey += "1"; // Concatenate "1" if true at location
				} else {
					binKey += "0"; // Concatenate "0" otherwise 
				}
			}
		}
		return binKey;
	}

	@Override
	public String[] dimensions() {  
		return new String[] {"Level Path"};
	}

	@Override
	public int[] dimensionSizes() { 
		return new int[] {maxSize};
	}
	
//	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
//		//MMNEAT.main("".split(" "));
//		GANProcess.type = GAN_TYPE.ZELDA;
//		Parameters.initializeParameterCollections(args);
//		Parameters.parameters.setInteger("zeldaGANLevelWidthChunks", 4);
//		Parameters.parameters.setInteger("zeldaGANLevelHeightChunks", 4);
//		LevelTraversalPathBinLabels lablers = new LevelTraversalPathBinLabels();
//		lablers.binLabels();
//	}

}
