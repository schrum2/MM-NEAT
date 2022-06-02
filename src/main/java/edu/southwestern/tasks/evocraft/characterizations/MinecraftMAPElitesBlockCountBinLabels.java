package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;
import java.util.List;

import edu.southwestern.tasks.evocraft.fitness.DepthFitness;
import edu.southwestern.tasks.evocraft.fitness.HeightFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.WidthFitness;

public class MinecraftMAPElitesBlockCountBinLabels extends MinecraftMAPElitesBinLabels {
	private List<String> labels = null;
	// TODO: find out if this should stay the same as from the other class file:
	// private List<MinecraftFitnessFunction> properties = Arrays.asList(new WidthFitness(), new HeightFitness(), new DepthFitness());
	
	@Override
	public List<String> binLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] dimensionSizes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		// TODO: uncomment this out once I find out the right properties.
		// return properties;
		return null;
	}

}
