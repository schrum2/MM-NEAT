package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;
/**
 * Minecraft shapes are categorized based on non-AIR blocks.
 *
 */
public class MinecraftMAPElitesBlockCountBinLabels extends MinecraftMAPElitesBinLabels {
	
	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new OccupiedCountFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int xDim = Parameters.parameters.integerParameter("minecraftXRange")+1;
			int yDim = Parameters.parameters.integerParameter("minecraftYRange")+1;
			int zDim = Parameters.parameters.integerParameter("minecraftZRange")+1;
			
			int size = xDim*yDim*zDim;
			
			labels = new ArrayList<String>(size);
			for(int i = 0; i < size + 1; i++) labels.add(i + " Blocks");
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int binIndex = multi[0];
		return binIndex;
	}

	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("minecraftXRange")+1, Parameters.parameters.integerParameter("minecraftYRange")+1, Parameters.parameters.integerParameter("minecraftZRange")+1};
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}

}
