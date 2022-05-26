package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

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
	
}
