package edu.southwestern.tasks.evocraft.mutation;

import java.util.ArrayList;
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
 * Takes two random blocks from the occupied block set and swaps them.
 * @author raffertyt
 *
 */
public class SwapBlocksMutation extends MinecraftShapeMutation {

	public SwapBlocksMutation() {
		super("minecraftSwapBlocksMutation");
	}

	@Override
	public void mutate(Genotype<Pair<HashMap<MinecraftCoordinates, Block>, HashSet<MinecraftCoordinates>>> genotype) {
		List<MinecraftCoordinates> occupiedCoordinates = genotype.getPhenotype().t1.keySet().stream().collect(Collectors.toList());
		if(occupiedCoordinates.size() >= 2) {
			ArrayList<MinecraftCoordinates> swapCoordinates = RandomNumbers.randomChoose(occupiedCoordinates, 2);
			((MinecraftShapeGenotype) genotype).swapBlocks(swapCoordinates.get(0), swapCoordinates.get(1));
		}
	}
}
