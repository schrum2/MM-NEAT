package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.networks.activationfunctions.HalfLinearPiecewiseFunction;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.BlockType;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
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
	public static MinecraftCoordinates generateBlock(MinecraftCoordinates corner, List<BlockType> blockSet, List<Block> blocks, Network net,
			MinecraftCoordinates ranges, boolean distanceInEachPlane, int xi, int yi, int zi) {
		double[] inputs = ThreeDimensionalUtil.get3DObjectCPPNInputs(xi, yi, zi, ranges.x(), ranges.y(), ranges.z(), -1, distanceInEachPlane);
		net.flush(); // There should not be any left over recurrent activation, but clear each time just in case
		double[] outputs = net.process(inputs);
		//if(SnakeGenerator.debug) System.out.println("("+xi+","+yi+","+zi+"):" + Arrays.toString(inputs) + " -> " + Arrays.toString(outputs));
		int numBlockTypes = blockSet.size();
		if(outputs[OUTPUT_INDEX_PRESENCE] > Parameters.parameters.doubleParameter("voxelExpressionThreshold")) {

			int typeIndex;
			if(Parameters.parameters.booleanParameter("oneOutputLabelForBlockTypeCPPN")) { // only one output will be used for the blocks
				typeIndex = (int) HalfLinearPiecewiseFunction.halfLinear(outputs[OUTPUT_INDEX_PRESENCE+1]) * numBlockTypes; 
			} else {
				ArrayList<Double> blockPreferences = new ArrayList<Double>(numBlockTypes);
				for(int i = 1; i <= numBlockTypes; i++) {
					blockPreferences.add(outputs[i]);
				}
				typeIndex = StatisticsUtilities.argmax(blockPreferences); // different outputs for each block type will be used
			}
	
			Orientation blockOrientation = Orientation.NORTH; // default orientation is North
			if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")){
				int numOrientations = MinecraftUtilClass.getnumOrientationDirections(); // will either be 2 or 6
				int orientationPreferenceIndex;
				if(Parameters.parameters.booleanParameter("oneOutputLabelForBlockOrientationCPPN")) { // only one output will be used for the orientation
					orientationPreferenceIndex = (int) HalfLinearPiecewiseFunction.halfLinear(outputs[OUTPUT_INDEX_PRESENCE+2]) * numOrientations;
				} else { // different outputs for each orientation direction will be used
					double[] orientationPreferences = ArrayUtil.portion(outputs,numBlockTypes + 1, numBlockTypes + numOrientations);
					assert orientationPreferences.length == numOrientations;
					orientationPreferenceIndex = StatisticsUtilities.argmax(orientationPreferences);	
				}
				blockOrientation = MinecraftUtilClass.getOrientations()[orientationPreferenceIndex];
			}
			
			Block b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.get(typeIndex), blockOrientation);
			blocks.add(b);
		} 

		if(MMNEAT.shapeGenerator instanceof SnakeGenerator) {
			int startIndex = outputs.length - NUM_DIRECTIONS - 1;
			int endIndex = outputs.length - 2;
			double[] directionPreferences = ArrayUtil.portion(outputs, startIndex, endIndex);
			assert directionPreferences.length == NUM_DIRECTIONS : "Should have 6 possible directions: " + Arrays.toString(directionPreferences) + " from "+ startIndex +" to " + endIndex + " of " + Arrays.toString(outputs);

			// If redirecting snakes when confining
			if(Parameters.parameters.booleanParameter("minecraftRedirectConfinedSnakes")) {
				for(int i = 0; i < NUM_DIRECTIONS; i++) {
					int[] possibleDirection = nextDirection(i);
					if(checkOutOfBounds(possibleDirection, ranges, xi, yi, zi)) {
						directionPreferences[i] = Double.NEGATIVE_INFINITY;
					}
				}
			}

			int directionIndex = StatisticsUtilities.argmax(directionPreferences);
			int[] direction = nextDirection(directionIndex);

			// If stopping the snakes when confining
			if(Parameters.parameters.booleanParameter("minecraftStopConfinedSnakes")){
				if(checkOutOfBounds(direction, ranges, xi, yi, zi)) {
					return null;
				}
			}
			
			// If the "continue" spot at the end is less than or equal the snake continuation threshold, STOP!
			if(outputs[outputs.length-1] <= SNAKE_CONTINUATION_THRESHOLD) {
				//System.out.println("Continue? " + outputs[outputs.length-1]);
				return null;
			}

			MinecraftCoordinates minecraftDirection = new MinecraftCoordinates(Integer.valueOf(direction[0]),Integer.valueOf(direction[1]),Integer.valueOf(direction[2]));
			return minecraftDirection;	
		} else {
			return null; // null result means to stop generation when used to evolve snakes
		}
	}

	/**
	 * Checks to see if the new possible position relative to the initial position is still
	 * in bounds
	 * 
	 * @param possibleDirection The next possible direction a block can be placed in
	 * @param ranges The ranges of the x, y, and z direction that confines a shape
	 * @param xi Initial x coordinate
	 * @param yi Initial y coordinate
	 * @param zi Initial z coordinate
	 * @return True if out of bounds, false otherwise
	 */
	public static boolean checkOutOfBounds(int[] possibleDirection, MinecraftCoordinates ranges, int xi, int yi, int zi) {
		return xi + possibleDirection[0] >= ranges.x() || xi + possibleDirection[0] < 0 ||
				yi + possibleDirection[1] >= ranges.y() || yi + possibleDirection[1] < 0 ||
				zi + possibleDirection[2] >= ranges.z() || zi + possibleDirection[2] < 0;
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

		if(Parameters.parameters.booleanParameter("oneOutputLabelForBlockTypeCPPN")) {
			labels = new String[2]; // only one label for block type
			labels[1] = "BlockType"; 
		} else { // presence output and an output for each block type
			labels = new String[1 + blockSet.getPossibleBlocks().length];	
			for(int i = 1; i < labels.length; i++) {
				labels[i] = blockSet.getPossibleBlocks()[i-1].name();
			}
		}
		labels[0] = "Presence"; // regardless Presence will be the first index
		
		if(Parameters.parameters.booleanParameter("minecraftEvolveOrientation")) {
			String[] orientationLabels;

			if(Parameters.parameters.booleanParameter("oneOutputLabelForBlockOrientationCPPN")) {
				orientationLabels = new String[1]; // only use one label for block orientation
				orientationLabels[0] = "Orientation";
			} else { // orientation label for each direction
				orientationLabels = new String[NUM_DIRECTIONS];
				for(int i = 0; i < orientationLabels.length; i++) {
					orientationLabels[i] = Orientation.values()[i].toString();
				}
			}

			labels = ArrayUtil.combineArrays(labels,orientationLabels);
		}
		return labels;
	}
}
