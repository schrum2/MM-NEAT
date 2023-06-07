package edu.southwestern.tasks.evocraft.shapegeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.blocks.BlockSet;

/**
 * Shape generator for MinecraftShapeGenotype. Takes a hash map and turns it into an ArrayList.
 * @author raffertyt
 *
 */
public class DirectRepresentationShapeGenerator implements ShapeGenerator<HashMap<MinecraftCoordinates, Block>> {
	

	@Override
	public List<Block> generateShape(Genotype<HashMap<MinecraftCoordinates, Block>> genome, MinecraftCoordinates corner,
			BlockSet blockSet) {
		Collection<Block> collectionOfBlocks =  genome.getPhenotype().values();
		ArrayList<Block> blocks = new ArrayList<>(collectionOfBlocks);
		return blocks;
	}

	@Override
	public String[] getNetworkOutputLabels() {
		throw new UnsupportedOperationException("This should not be called for DirectRepresentationShapeGenerator");
	}


	

}
