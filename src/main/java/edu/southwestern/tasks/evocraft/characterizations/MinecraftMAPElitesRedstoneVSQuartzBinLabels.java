package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;

import edu.southwestern.tasks.evocraft.fitness.NumQuartzFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;

/**
 * Class that extends MinecraftMAPElites2DBlockCountBinLabels and sets properties = Redstone and Quartz
 * @author schrum2
 *
 */
public class MinecraftMAPElitesRedstoneVSQuartzBinLabels extends MinecraftMAPElites2DBlockCountBinLabels{

	public MinecraftMAPElitesRedstoneVSQuartzBinLabels() {
		super(Arrays.asList(new NumRedstoneFitness(), new NumQuartzFitness()));
	}

	
}
