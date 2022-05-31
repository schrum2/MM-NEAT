package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.networks.Network;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftClient.Orientation;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;
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
	 * 
	 * @param corner
	 * @param blockSet
	 * @param blocks List of blocks to add each generated block to
	 * @param net CPPN that is queried to generate blocks
	 * @param ranges
	 * @param distanceInEachPlane Whether CPPN takes additional center distance inputs for each plane
	 * @param xi
	 * @param yi
	 * @param zi
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
			Block b = new Block(corner.add(new MinecraftCoordinates(xi,yi,zi)), blockSet.getPossibleBlocks()[typeIndex], blockOrientation);
			blocks.add(b);
		}
		
		return null; // null result means to stop generation when used to evolve snakes
	}

}
