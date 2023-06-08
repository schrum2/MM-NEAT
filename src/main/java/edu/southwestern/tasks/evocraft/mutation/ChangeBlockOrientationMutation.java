package edu.southwestern.tasks.evocraft.mutation;

import java.util.HashMap;
import java.util.HashSet;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
/**
 * Removes a block at a random coordinate in the genotype based on the rate set in the parameter.
 * @author raffertyt
 *
 */
public class ChangeBlockOrientationMutation extends MinecraftShapeMutation {

	public ChangeBlockOrientationMutation() {
		super("ChangeBlockOrientationMutation");
	}

	@Override
	public void mutate(Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genotype) {
		MinecraftShapeGenotype shapeGenotype = (MinecraftShapeGenotype) genotype;
		MinecraftCoordinates randomCoordinates = MinecraftUtilClass.randomCoordinatesInShapeRange();
		shapeGenotype.removeBlock(randomCoordinates);
	}
	
}
