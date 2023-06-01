package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;

import edu.southwestern.tasks.evocraft.fitness.NumAirFitness;
import edu.southwestern.tasks.evocraft.fitness.NumLavaFitness;
import edu.southwestern.tasks.evocraft.fitness.NumPistonsFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;
import edu.southwestern.tasks.evocraft.fitness.NumWaterFitness;

/**
 * Class that extends MinecraftMAPElites2DBlockCountBinLabels and sets properties = Redstone and pistons
 * @author raffertyt
 *
 */
public class MinecraftMAPElitesAirVSPistonBinLabels extends MinecraftMAPElites2DBlockCountBinLabels{

	public MinecraftMAPElitesAirVSPistonBinLabels() {
		super(Arrays.asList(new NumAirFitness(), new NumPistonsFitness()));
	}

	
}
