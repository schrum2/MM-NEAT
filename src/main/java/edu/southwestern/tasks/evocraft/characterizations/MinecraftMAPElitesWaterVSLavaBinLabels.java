package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;

import edu.southwestern.tasks.evocraft.fitness.NumLavaFitness;
import edu.southwestern.tasks.evocraft.fitness.NumPistonsFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;
import edu.southwestern.tasks.evocraft.fitness.NumWaterFitness;

/**
 * Class that extends MinecraftMAPElites2DBlockCountBinLabels and sets properties = Redstone and pistons
 * @author raffertyt
 *
 */
public class MinecraftMAPElitesWaterVSLavaBinLabels extends MinecraftMAPElites2DBlockCountBinLabels{

	public MinecraftMAPElitesWaterVSLavaBinLabels() {
		super(Arrays.asList(new NumLavaFitness(), new NumWaterFitness()));
	}

	
}
