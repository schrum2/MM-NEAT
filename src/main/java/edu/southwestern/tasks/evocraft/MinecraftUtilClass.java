package edu.southwestern.tasks.evocraft;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 * Some commonly used methods for dealing with the Minecraft world
 * It's a bit unclear when a method should be here rather than MinecraftClient,
 * but both have methods used in multiple places
 * 
 * @author Jacob Schrum
 *
 */
public class MinecraftUtilClass {

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
	 * Size of space around each shape, which includes all the empty space between shapes
	 * @return Save of space including empty space between shapes
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
	
	public static List<Block> readBlocksFromClient(MinecraftCoordinates corner, MinecraftCoordinates ranges) {
		MinecraftClient client = MinecraftClient.getMinecraftClient();
		List<Block> blocks = client.readCube(corner, corner.add(ranges));
		return blocks;
	}
	
	/**
	 * Block orientations cannot be read from the world, so they are always null when read. 
	 * To compare against such lists, it might be necessary to take a list with non-null
	 * block orientations and set them to null. This means there is no certainty as you
	 * what the orientations are, but it gives some means of comparison.
	 * 
	 * @param originalBlocks Blocks from a generator, that still have orientations
	 * @return block list with only null orientations, but otherwise the same
	 */
	public static List<Block> wipeOrientations(List<Block> originalBlocks) {
		ArrayList<Block> result = new ArrayList<>(originalBlocks.size());
		for(Block b : originalBlocks) {
			result.add(new Block(b.x(), b.y(), b.z(),b.type()));
		}
		return result;
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
		List<Block> blocks = new ArrayList<Block>();
		Scanner s = new Scanner(f);
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
			int x = Integer.parseInt(coordinates[0].substring(1)); // gets rid of '(' from x coordinate sub-token
			int y = Integer.parseInt(coordinates[1]); // y coordinate
			int z = Integer.parseInt(coordinates[2].substring(0, coordinates[2].length()-1)); // gets rid of ')' from z coordinate
			
			String orientation = blockVals[2].substring(0, blockVals[2].length()-1); // gets rid of "]" from orientation token
			
			// b is the new block to add to blocks
			Block b = new Block(new MinecraftCoordinates(x,y,z), BlockType.valueOf(bType), Orientation.valueOf(orientation));
			blocks.add(b);		
		}
		s.close(); // close the scanner
		// System.out.println(blocks);
		return blocks;
	}
}
