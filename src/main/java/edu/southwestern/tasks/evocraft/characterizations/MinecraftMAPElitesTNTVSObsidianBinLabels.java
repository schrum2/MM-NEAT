package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;

import edu.southwestern.tasks.evocraft.fitness.NumPistonsFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;

/**
 * Class that extends MinecraftMAPElites2DBlockCountBinLabels and sets properties = Redstone and pistons
 * @author raffertyt
 *
 */
public class MinecraftMAPElitesTNTVSObsidianBinLabels extends MinecraftMAPElites2DBlockCountBinLabels{

	public MinecraftMAPElitesTNTVSObsidianBinLabels() {
		super(Arrays.asList(new NumRedstoneFitness(), new NumPistonsFitness()));
	}

	
}
