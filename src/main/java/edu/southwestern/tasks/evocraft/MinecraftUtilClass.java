package edu.southwestern.tasks.evocraft;

import edu.southwestern.parameters.Parameters;
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
}
