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
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
/**
 * Takes a random block from the occupied set and changes its orientation to a random orientation.
 * @author raffertyt
 *
 */
public class ChangeBlockTypeMutation extends MinecraftShapeMutation {

	public ChangeBlockTypeMutation() {
		super("minecraftChangeBlockTypeMutation");
	}

	@Override
	public void mutate(Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genotype) {
		List<MinecraftCoordinates> occupiedCoordinates = genotype.getPhenotype().t1.keySet().stream().collect(Collectors.toList());
		if(!occupiedCoordinates.isEmpty()) {
			MinecraftCoordinates toChange = RandomNumbers.randomElement(occupiedCoordinates);
			((MinecraftShapeGenotype) genotype).changeBlockType(toChange, MinecraftShapeGenotype.randomBlockType());
		}
	}
	
}
