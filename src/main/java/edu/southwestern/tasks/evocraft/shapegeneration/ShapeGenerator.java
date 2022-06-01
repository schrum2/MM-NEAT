package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.graphics.ThreeDimensionalUtil;
import edu.southwestern.util.stats.StatisticsUtilities;

/**
 * Defines an approach to generating a list of Blocks to place in Minecraft
 * based on an evolved genome.
 * 
 * @author Jacob Schrum
 *
 * @param <T>
 */
public interface ShapeGenerator<T> {

	public static final int OUTPUT_INDEX_PRESENCE = 0;
	public static final double VOXEL_EXPRESSION_THRESHOLD = 0.1;
	public static final double SNAKE_CONTINUATION_THRESHOLD = 0.1;
	public static final int NUM_DIRECTIONS = 6;
	
	
	/**
	 * Take an evolved genome and generate a shape at a location relative
	 * to the coordinates in the corner parameter.
	 * 
	 * @param genome Evolved genome
	 * @param corner Location in world to generate the shape
	 * @param blockSet possible blocks that can be generated
	 * @return List of Blocks to generate
	 */
	public List<Block> generateShape(Genotype<T> genome, MinecraftCoordinates corner, BlockSet blockSet);
	
	/**
	 * Array of the names of the CPPN output neurons
	 * @return Array of output labels
	 */
	public String[] getNetworkOutputLabels();
	
	/**
	 * Returns a MinecraftCoordinate that indicates the next direction
	 * to place a block. For snakes, if the value of null is returned,
	 * that means that the snake will stop generating. 
	 * 
	 * @param corner Location in world to generate the shape
	 * @param blockSet possible blocks that can be generated
	 * @param blocks List of blocks to add each generated block to
	 * @param net CPPN that is queried to generate blocks
	 * @param ranges Ranges for the x, y, and z direction where blocks can be placed
	 * @param distanceInEachPlane Whether CPPN takes additional center distance inputs for each plane
	 * @param xi Initial x coordinate
	 * @param yi Initial y coordinate
	 * @param zi Initial z coordinate
	 * @return Coordinates that indicate the direction to move in next for snake generation, where a null result indicates
	 * 			that shape generation should stop. This is not used for standard 3D volume generation.
	 */
	public static MinecraftCoordinates generateBlock(MinecraftCoordinates corner, BlockSet blockSet, List<Block> blocks, Network net,
			MinecraftCoordinates ranges, boolean distanceInEachPlane, int xi, int yi, int zi) {
		double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
		double[] outputs = net.process(inputs);
		int numBlockTypes = blockSet.getPossibleBlocks().length;
		if(outputs[OUTPUT_INDEX_PRESENCE] > VOXEL_EXPRESSION_THRESHOLD) {
			ArrayList<Double> blockPreferences = new ArrayList<Double>(numBlockTypes);
			for(int i = 1; i <= numBlockTypes; i++) {
				blockPreferences.add(outputs[i]);
			}
			int typeIndex = StatisticsUtilities.argmax(blockPreferences);
			// TODO: Add way to evolve orientation
			Orientation blockOrientation = Orientation.NORTH;
			if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")){
				double[] orientationPreferences;
				if(MMNEAT.shapeGenerator instanceof SnakeGenerator) {
					orientationPreferences = ArrayUtil.portion(outputs, numBlockTypes + NUM_DIRECTIONS + 1, numBlockTypes + NUM_DIRECTIONS*2);
					assert orientationPreferences.length == 6 : "Should have 6 possible directions: " + Arrays.toString(orientationPreferences) + " from "+ (numBlockTypes + NUM_DIRECTIONS + 1) +" to " + (numBlockTypes + NUM_DIRECTIONS*2) + " of " + Arrays.toString(outputs);
				} else {
					orientationPreferences = ArrayUtil.portion(outputs,numBlockTypes + 1, numBlockTypes + NUM_DIRECTIONS);
				}
				int orientationPreferenceIndex = StatisticsUtilities.argmax(orientationPreferences);
				blockOrientation = Orientation.values()[orientationPreferenceIndex];
			}
			Block b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[typeIndex], blockOrientation);
			blocks.add(b);
		} 
			
		if(MMNEAT.shapeGenerator instanceof SnakeGenerator) {
			int startIndex = numBlockTypes + 1;
			int endIndex = numBlockTypes + NUM_DIRECTIONS;
			double[] directionPreferences = ArrayUtil.portion(outputs, startIndex, endIndex);
			assert directionPreferences.length == 6 : "Should have 6 possible directions: " + Arrays.toString(directionPreferences) + " from "+ startIndex +" to " + endIndex + " of " + Arrays.toString(outputs);
			int directionIndex = StatisticsUtilities.argmax(directionPreferences);
			int[] possibleDirection = nextDirection(directionIndex);
			MinecraftCoordinates minecraftDirection = new MinecraftCoordinates(Integer.valueOf(possibleDirection[0]),Integer.valueOf(possibleDirection[1]),Integer.valueOf(possibleDirection[2]));
			return minecraftDirection;	
		} else {
			return null; // null result means to stop generation when used to evolve snakes
		}
	}

	/**
	 * Vector direction associated with given index in direction preferences.
	 * 
	 * @param directionIndex index in collection of direction preferences from CPPN
	 * @return length-3-array representing the movement direction in (x,y,z) coordinates for the given index.
	 */
	public static int[] nextDirection(int directionIndex) {
		int[] direction = {0,0,0};
		direction[directionIndex % 3] = directionIndex < 3 ?  -1 : 1;
		return direction;
	}

	/**
	 * Returns a string array of labels that represents all the presence outputs,
	 * output for each block type, output for the next direction in the negative
	 * and positive x, y, and z directions, and the output for continuation.
	 * 
	 * @param blockSet Possible blocks that can be generated
	 * @return String array that contains the labels for the outputs
	 */
	public static String[] defaultNetworkOutputLabels(BlockSet blockSet) {
		String[] labels;
		if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) {
			
			labels = new String[1 + blockSet.getPossibleBlocks().length + NUM_DIRECTIONS];
			labels[0] = "Presence";
			
			for(int i = 1; i < blockSet.getPossibleBlocks().length; i++) {
				labels[i] = blockSet.getPossibleBlocks()[i-1].name();
			}
			
			for(int i = blockSet.getPossibleBlocks().length, j = 0; i < labels.length-1; i++, j++) {
				labels[i] = Orientation.values()[j].toString();
			}

		} else {
			// Presence output and an output for each block type
			labels = new String[1 + blockSet.getPossibleBlocks().length];
			labels[0] = "Presence";
			for(int i = 1; i < labels.length; i++) {
				labels[i] = blockSet.getPossibleBlocks()[i-1].name();
			}
		}
		
		return labels;
	}

}
