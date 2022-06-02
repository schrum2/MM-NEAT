package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.DepthFitness;
import edu.southwestern.tasks.evocraft.fitness.HeightFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.WidthFitness;

/**
 * Minecraft shapes are categorized based on width, height, and depth.
 * @author schrum2
 *
 */
public class MinecraftMAPElitesWidthHeightDepthBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new WidthFitness(), new HeightFitness(), new DepthFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int xDim = Parameters.parameters.integerParameter("minecraftXRange")+1;
			int yDim = Parameters.parameters.integerParameter("minecraftYRange")+1;
			int zDim = Parameters.parameters.integerParameter("minecraftZRange")+1;
			
			int size = xDim*yDim*zDim;
			labels = new ArrayList<String>(size);
			for(int i = 0; i < xDim; i++) {
				for(int j = 0; j < yDim; j++) {
					for(int k = 0; k < zDim; k++) {
						labels.add("W"+i+"H"+j+"D"+k);
					}
				}	
			}		
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int yDim = Parameters.parameters.integerParameter("minecraftYRange")+1;
		int zDim = Parameters.parameters.integerParameter("minecraftZRange")+1;
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];
		return binIndex;
	}


	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("minecraftXRange")+1, Parameters.parameters.integerParameter("minecraftYRange")+1, Parameters.parameters.integerParameter("minecraftZRange")+1};
	}

	/**
	 * Collection of fitness functions that calculate scores to based the
	 * behavior characterization on.
	 * @return List of fitness functions for Minecraft
	 */
	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}
}
