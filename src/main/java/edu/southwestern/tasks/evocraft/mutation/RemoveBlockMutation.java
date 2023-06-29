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
public class RemoveBlockMutation extends MinecraftShapeMutation {

	public RemoveBlockMutation() {
		super("minecraftRemoveBlockMutationRate");
	}

	@Override
	public void mutate(Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genotype) {
		List<MinecraftCoordinates> occupiedCoordinates = genotype.getPhenotype().t1.keySet().stream().collect(Collectors.toList());
		if(!occupiedCoordinates.isEmpty()) {
			MinecraftCoordinates toReplace = RandomNumbers.randomElement(occupiedCoordinates);
			((MinecraftShapeGenotype) genotype).removeBlock(toReplace);
		}
	}	
}
