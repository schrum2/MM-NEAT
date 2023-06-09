package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import cern.colt.Arrays;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Vertex;
import edu.southwestern.util.file.FileUtilities;
import edu.southwestern.util.random.RandomNumbers;

/**
 * Some commonly used methods for dealing with the Minecraft world
 * It's a bit unclear when a method should be here rather than MinecraftClient,
 * but both have methods used in multiple places
 * 
 * @author Jacob Schrum
 *
 */
public class MinecraftUtilClass {

	//empty space is all the space between shape evaluation areas
	public static int emptySpaceOffsetX() {
		return emptySpaceOffset(Parameters.parameters.integerParameter("minecraftXRange"));
	}

	public static int emptySpaceOffsetY() {
		return emptySpaceOffset(Parameters.parameters.integerParameter("minecraftYRange"));
	}

	public static int emptySpaceOffsetZ() {
		return emptySpaceOffset(Parameters.parameters.integerParameter("minecraftZRange"));
	}
	
	/**
	 * For a given dimension, the empty space offset is half the total space between shapes, 
	 * but minus half the space that the shape could actually occupy.
	 * 
	 * @param range number of blocks a shape could actually occupy in some direction
	 * @return number of blocks between reserved space start and possible shape start
	 */
	public static int emptySpaceOffset(int range) {
		return (int) (((range + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")) / 2.0) - (range/2.0));
	}
	
	/**
	 * How far away in each coordinate the minimal corner of where the shape is generated is
	 * from the minimal corner of cleared out space reserved for the shape.
	 * @return Coordinates of distance between minimal corner of empty space and minimal corner of where shape is generated
	 */
	public static MinecraftCoordinates emptySpaceOffsets() {
		return new MinecraftCoordinates(emptySpaceOffsetX(),emptySpaceOffsetY(),emptySpaceOffsetZ());
	}
	
	/**
	 * Size of space reserved for shape in each coordinate
	 * @return Size of space shape is generated within
	 */
	public static MinecraftCoordinates getRanges() { 
		return new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange"),
				Parameters.parameters.integerParameter("minecraftYRange"),
				Parameters.parameters.integerParameter("minecraftZRange"));
	}
	
	/**
	 * Generates random coordinates within the shape ranges
	 * @return random coordinates
	 */
	public static MinecraftCoordinates randomCoordinatesInShapeRange() {
		return new MinecraftCoordinates(
				RandomNumbers.randomGenerator.nextInt(Parameters.parameters.integerParameter("minecraftXRange")),
				RandomNumbers.randomGenerator.nextInt(Parameters.parameters.integerParameter("minecraftYRange")),
				RandomNumbers.randomGenerator.nextInt(Parameters.parameters.integerParameter("minecraftZRange")));
	}
	
	/**
	 * Number of blocks that are reserved for each generated shape
	 * @return total number of blocks
	 */
	public static int numberOfBlocksPossibleInShape() {
		MinecraftCoordinates ranges = getRanges();
		return ranges.x() * ranges.y() * ranges.z();
	}

	/**
	 * Size of space around each shape, which includes all the empty space between shapes
	 * @return Size of space including empty space between shapes
	 */
	public static MinecraftCoordinates reservedSpace() {
		final int SPACE_BETWEEN = Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes");
		// Makes the buffer space between coordinates
		MinecraftCoordinates bufferDist = new MinecraftCoordinates(SPACE_BETWEEN,SPACE_BETWEEN,SPACE_BETWEEN);
		// End coordinate is based on buffer distance. Then shape is cleared
		return bufferDist.add(getRanges());
	}
	
	/**
	 * Overloaded method. Takes 6 ints and makes them into minecraft coordinates to call volume
	 * 
	 * @param xmin Minimal x coordinate. xmin <= xmax
	 * @param ymin Minimal y coordinate. ymin <= ymax
	 * @param zmin Minimal z coordinate. zmin <= zmax
	 * @param xmax Maximal x coordinate
	 * @param ymax Maximal y coordinate
	 * @param zmax Maximal z coordinate
	 * @return 
	 */
	public static int volume(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
		MinecraftCoordinates max = new MinecraftCoordinates(xmax,ymax,zmax);
		MinecraftCoordinates min = new MinecraftCoordinates(xmin,ymin,zmin);
		return volume(min,max);
	}
	
	/**
	 * Computes the volume of two minecraft coordinates
	 * 
	 * @param min Coordinate containing the min values
	 * @param max Coordinate containing the min values
	 * @return The volume computed from these values
	 */
	public static int volume(MinecraftCoordinates min, MinecraftCoordinates max) {
		int volume = (max.x()-min.x()+1)*(max.y()-min.y()+1)*(max.z()-min.z()+1);
		return volume;
	}
	
	/**
	 * For two points, get one point that has the minimum coordinate across all listed points
	 * @param c1 point 
	 * @param c2 other point
	 * @return minimal point
	 */
	public static MinecraftCoordinates minCoordinates(MinecraftCoordinates c1, MinecraftCoordinates c2) {
		return new MinecraftCoordinates(Math.min(c1.x(), c2.x()), Math.min(c1.y(), c2.y()), Math.min(c1.z(), c2.z()));
	}
	
	/**
	 * Across a list of blocks, find the minimal coordinate across all of their positions
	 * @param blocks List of blocks
	 * @return Corner that they were generated at
	 */
	public static MinecraftCoordinates minCoordinates(List<Block> blocks) {
		MinecraftCoordinates result = blocks.get(0).position;
		for(int i = 1; i < blocks.size(); i++) {
			result = minCoordinates(result, blocks.get(i).position);
		}
		return result;
	}
	/**
	 * For two points, get one point that has the maximum coordinate across all listed points
	 * @param c1 point 
	 * @param c2 other point
	 * @return maximum point
	 */
	public static MinecraftCoordinates maxCoordinates(MinecraftCoordinates c1, MinecraftCoordinates c2) {
		return new MinecraftCoordinates(Math.max(c1.x(), c2.x()), Math.max(c1.y(), c2.y()), Math.max(c1.z(), c2.z()));
	}
	
	/**
	 * Across a list of blocks, find the maximum coordinate across all of their positions
	 * @param blocks List of blocks
	 * @return Corner that they were generated at
	 */
	public static MinecraftCoordinates maxCoordinates(List<Block> blocks) {
		MinecraftCoordinates result = blocks.get(0).position;
		for(int i = 1; i < blocks.size(); i++) {
			result = maxCoordinates(result, blocks.get(i).position);
		}
		return result;
	}
	
	
	/**
	 * Remove all blocks of a given type from a list of blocks
	 * @param blocks Original blocks
	 * @param type Type to remove
	 * @return List with blocks removed
	 */
	public static List<Block> filterOutBlock(List<Block> blocks, BlockType type) {
		return blocks.stream().filter(b -> b.type() != type.ordinal()).collect(Collectors.toList());
	}
	
	
	/**
	 * Filter the blocks to only instances of block types in the typesToKeep
	 * 
	 * @param blocks List of various blocks
	 * @param type Array of block types to keep
	 * @return Filtered list with only blocks from the desired array of types
	 */
	public static List<Block> getDesiredBlocks(List<Block> blocks, BlockType[] typesToKeep) {
		return blocks.stream().filter(b -> ArrayUtil.contains(typesToKeep, BlockType.values()[b.type()])  ).collect(Collectors.toList());
	}
	
	/** 
	 * This static method will either return a restricted array of orientations, or
	 * it will return an array of orientations with all 6 orientations.
	 * 
	 * @return Array of orientations used for shape generation
	 */
	public static Orientation[] getOrientations() {
		Orientation[] orientations;
		if(Parameters.parameters.booleanParameter("minecraftNorthSouthOnly")) { // only use north and south orientations
			orientations = new Orientation[2];
			orientations[0] = Orientation.NORTH;
			orientations[1] = Orientation.SOUTH;
		} else if(Parameters.parameters.booleanParameter("minecraftUpDownOnly")) { // only use up and down orientations
			orientations = new Orientation[2];
			orientations[0] = Orientation.UP;
			orientations[1] = Orientation.DOWN;
		} else { // use normal orientation array
			orientations = Orientation.values();
		}
		return orientations;
	}
	
	/** 
	 * Returns the integer value 2 if the command line parameter "minecraftNorthSouthOnly" is true, otherwise returns 6.
	 *
	 * @return int Number of orientation directions (either restricted or not restricted)
	 */
	public static int getnumOrientationDirections() { 
		int result = 6;
		if(Parameters.parameters.booleanParameter("minecraftNorthSouthOnly") || Parameters.parameters.booleanParameter("minecraftUpDownOnly")) result = 2;
		return result; 
	}
	
	/**
	 * Use EvoCraft client code to call readCube and determine blocks that are
	 * present in the world.
	 * calculates range of evaluation area for secondary related function readBlocksFromClient
	 * 
	 * @param corner minimal coordinate of shape being checked
	 * @return List of blocks occupying the space for the given shape
	 */
	public static List<Block> readBlocksFromClient(MinecraftCoordinates corner) {
		MinecraftCoordinates ranges = new MinecraftCoordinates(
				Parameters.parameters.integerParameter("minecraftXRange") - 1,
				Parameters.parameters.integerParameter("minecraftYRange") - 1,
				Parameters.parameters.integerParameter("minecraftZRange") - 1);
		return readBlocksFromClient(corner, ranges);
	}
	
	/**
	 * Use EvoCraft client code to call readCube and determine blocks that are 
	 * present in the world.
	 * Called by readBlocksFromClient 
	 * uses range previously calculated for evaluation area & uses client code to call readCube
	 * 
	 * TODO: shouldn't this be moved to MinecraftClients?
	 * 
	 * -Joanna
	 * 
	 * @param corner minimal coordinates of shape being checked
	 * @param ranges coordinates or passed for space to evaluate
	 * @return list of blocks occupying the space for the given shape
	 */
	public static List<Block> readBlocksFromClient(MinecraftCoordinates corner, MinecraftCoordinates ranges) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		List<Block> blocks = client.readCube(corner, corner.add(ranges));
		return blocks;
	}
	
	/**
	 * Block orientations cannot be read from the world, so they are always null when read. 
	 * To compare against such lists, it might be necessary to take a list with non-null
	 * block orientations and set them to null. This means there is no certainty as to
	 * what the orientations are, but it gives some means of comparison.
	 * 
	 * @param originalBlocks Blocks from a generator, that still have orientations
	 * @return block list with only null orientations, but otherwise the same
	 */
	public static List<Block> wipeOrientations(List<Block> originalBlocks) {
		ArrayList<Block> blockListWithoutOrientation = new ArrayList<>(originalBlocks.size());
		for(Block b : originalBlocks) {
			blockListWithoutOrientation.add(new Block(b.x(), b.y(), b.z(),b.type()));
		}
		return blockListWithoutOrientation;
	}
	
	/**
	 * Returns list of blocks from a previously loaded
	 * MAP-Elites output text file.
	 * 
	 * @param f file to be read in
	 * @return List of blocks to be spawned in the world.
	 * @throws FileNotFoundException
	 */
	public static List<Block> loadMAPElitesOutputFile(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		List<Block> blocks = readMinecraftBlockListFromScanner(s);
		s.close(); // close the scanner
		// System.out.println(blocks);
		return blocks;
	}

	/**
	 * Read String containing a Minecraft block list and construct the corresponding block list
	 * 
	 * @param s string describing list of Minecraft blocks
	 * @return the list of blocks
	 */
	public static List<Block> readMinecraftBlockListFromString(String s){
		Scanner scan = new Scanner(s);
		List<Block> blocks = readMinecraftBlockListFromScanner(scan);
		scan.close();
		return blocks;
	}
	
	/**
	 * Read output containing a Minecraft block list from a Scanner and construct the corresponding block list
	 * 
	 * @param s Scanner of raw text that describes a list of Minecrafy blocks in a shape
	 * @return Corresponding list of Minecraft blocks
	 */
	public static List<Block> readMinecraftBlockListFromScanner(Scanner s) {
		List<Block> blocks = new ArrayList<Block>();
		boolean start = true; // used because fencepost problem when parsing (starting "[" and ending "]" cause issues since this extra output is only at the beginning and end)
		while(s.hasNext()) {
			String line = ""; // empty string to begin with, will have 5 tokens per block (based on file output)
			
			for(int i = 0; i < 5; i++) {
				if(i % 2 == 0) line += s.next() + " "; // even index of i is useful information
				else s.next(); // odd index of i is useless information
			}
			
			// first token is [BLOCKTYPE
			// second token is (x,y,z)
			// third token is ORIENTATION,
			
			String[] blockVals = line.split(" "); // blockVals now has blockType, Coordinates, and orientation
			
			String bType;
			if(start) {
				bType = blockVals[0].substring(1); // gets rid of the '[' from the block type token
				start = false;
			}
			else {
				bType = blockVals[0]; // middle of list, no starting "["
			}
			
			String[] coordinates = blockVals[1].split(","); // splits coordinates token into the three coordinates x/y/z 
			try {
				int x = Integer.parseInt(coordinates[0].substring(1)); // gets rid of '(' from x coordinate sub-token
				int y = Integer.parseInt(coordinates[1]); // y coordinate
				int z = Integer.parseInt(coordinates[2].substring(0, coordinates[2].length()-1)); // gets rid of ')' from z coordinate

				String orientation = blockVals[2].substring(0, blockVals[2].length()-1); // gets rid of "]" from orientation token

				// b is the new block to add to blocks
				Block b = new Block(new MinecraftCoordinates(x,y,z), BlockType.valueOf(bType), Orientation.valueOf(orientation));
				blocks.add(b);
			} catch(NumberFormatException e) {
				System.out.println(line);
				System.out.println(Arrays.toString(blockVals));
				System.out.println(Arrays.toString(coordinates));
				s.close(); // closes the scanner
				throw e;
			}
		}
		return blocks;
	}

	/**
	 * Take blocks meant to be spawned at the original corner, and change their coordinates so that the same shape will be spawned at the destination corner.
	 * 
	 * @param originalBlocks Blocks to spawn
	 * @param originalCorner Corner where they spawn
	 * @param destinationCorner Corner to move them to
	 * @return Same blocks with shifted coordinates
	 */
	public static List<Block> shiftBlocksBetweenCorners(List<Block> originalBlocks, MinecraftCoordinates originalCorner, MinecraftCoordinates destinationCorner) {
		ArrayList<Block> result = new ArrayList<Block>(originalBlocks.size());
		MinecraftCoordinates change = destinationCorner.sub(originalCorner);
		for(Block b : originalBlocks) {
			MinecraftCoordinates newCoordinates = b.position.add(change);
			result.add(new Block(newCoordinates, b.type, b.orientation));
		}
		return result;
	}
	
	/**
	 * Calculate center of mass for a list of blocks in a shape by averaging the 
	 * x, y, and z coordinates across all non-AIR blocks in the shape.
	 * 
	 * @param blocks List of blocks in a shape
	 * @return Center of mass of the shape (assumes all blocks have uniform mass)
	 */
	public static Vertex getCenterOfMass(List<Block> blocks) {
		double x = 0;
		double y = 0;
		double z = 0;

		List<Block> filteredBlocks = MinecraftUtilClass.filterOutBlock(blocks,BlockType.AIR);

		for(Block b : filteredBlocks) {
			x += b.x();
			y += b.y();
			z += b.z();
		}

		double avgX = x/filteredBlocks.size();
		double avgY = y/filteredBlocks.size();
		double avgZ = z/filteredBlocks.size();

		Vertex centerOfMass = new Vertex(avgX,avgY,avgZ);

		return centerOfMass;
	}	
	
	/**
	 * Get all the blocks that are in Shape 1 but not in Shape 2.
	 * Blocks are only considered the same if they match in terms of position,
	 * type, and orientation.
	 * 
	 * @param shape1 A list of blocks representing a shape
	 * @param shape2 A list of blocks representing a shape
	 * @return A list of blocks from shape 1, but excluding shared/common blocks with shape 2
	 */
	public static List<Block> shapeListDifference(List<Block> shape1, List<Block> shape2) {
		HashSet<Block> shape1Blocks = new HashSet<>();
		for(Block b : shape1) shape1Blocks.add(b); // Get all blocks from Shape 1
		for(Block b : shape2) {
			// Remove all blocks from Shape 2 from the set of Shape 1 blocks.
			// If block is not present, then attempt simply returns false and does nothing.
			shape1Blocks.remove(b);
		}
		// Put remaining blocks back in a List
		ArrayList<Block> result = new ArrayList<>(shape1Blocks.size());
		for(Block b : shape1Blocks) {
			result.add(b);
		}
		return result;
	}
	
	//TODO: general shape saving function
	//makes a directory
	public static void saveShapes(List<Block> shapeToSaveBlockList , String directoryNameString) {
		//make the directory first
		String directoryString = FileUtilities.getSaveDirectory() + directoryNameString;
		File directoryFile = new File(directoryString);
		if(!directoryFile.exists() ) {
			directoryFile.mkdir();
		}
		
		//save block list?
//		String gen = "GEN"+(MMNEAT.ea instanceof GenerationalEA ? ((GenerationalEA) MMNEAT.ea).currentGeneration() : "ME");
//		MinecraftLonerShapeTask.writeBlockListFile(shapeToSaveBlockList, directoryString + File.separator + "ID"+genome.getId(), ".txt");
		/**
		 * 	String flyingDir = FileUtilities.getSaveDirectory() + "/flyingMachines";
			File dir = new File(flyingDir);	// Create dir
			if (!dir.exists()) {
				dir.mkdir();
			}
			//Orientation flyingDirection = directionOfMaximumDisplacement(deltaX,deltaY,deltaZ);
			//String gen = "GEN"+(MMNEAT.ea instanceof GenerationalEA ? ((GenerationalEA) MMNEAT.ea).currentGeneration() : "ME");
			MinecraftLonerShapeTask.writeBlockListFile(blocks, flyingDir + File.separator + "ID"+genome.getId(), ".txt");
		 */
		
	}
}
