package edu.southwestern.tasks.evocraft.mutation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.tasks.evocraft.MinecraftClient.Block;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.genotype.MinecraftShapeGenotype;
import edu.southwestern.util.datastructures.Pair;
import edu.southwestern.util.random.RandomNumbers;
/**
 * Removes a block at a random coordinate in the genotype based on the rate set in the parameter.
 * @author raffertyt
 *
 */
public class AddBlockMutation extends MinecraftShapeMutation {

	public AddBlockMutation() {
		super("minecraftAddBlockMutation");
	}

	@Override
	public void mutate(Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genotype) {
		List<MinecraftCoordinates> emptyCoordinates = genotype.getPhenotype().t2.stream().collect(Collectors.toList());
		if(!emptyCoordinates.isEmpty()) {
			MinecraftCoordinates toReplace = RandomNumbers.randomElement(emptyCoordinates);
			((MinecraftShapeGenotype) genotype).addBlock(toReplace, MinecraftShapeGenotype.randomBlockType(), MinecraftShapeGenotype.randomBlockOrientation());
		}		
	}
	
}
