package edu.southwestern.tasks.evocraft;

import java.util.List;
import java.util.stream.Collectors;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;

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
}
