package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			int size = xDim*yDim*zDim;
			labels = new ArrayList<String>(size);
			for(int i = 1; i <= xDim; i++) {
				for(int j = 1; j <= yDim; j++) {
					for(int k = 1; k <= zDim; k++) {
						labels.add("W"+i+"H"+j+"D"+k);
					}
				}	
			}		
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		int yDim = Parameters.parameters.integerParameter("minecraftYRange");
		int zDim = Parameters.parameters.integerParameter("minecraftZRange");
		int binIndex = (multi[0]-1)*yDim*zDim + (multi[1]-1)*zDim + (multi[2]-1);
		assert binIndex < labels.size() : binIndex + " from " + Arrays.toString(multi) + ":yDim="+yDim+":zDim="+zDim;
		// Not safe to assert this here. Sometimes, the illegal index will be calculated, even though it should never be used
		//assert binIndex >= 0 : binIndex + " from " + Arrays.toString(multi) + ":yDim="+yDim+":zDim="+zDim;
		return binIndex;
	}


	@Override
	public int[] dimensionSizes() {
		return new int[] {Parameters.parameters.integerParameter("minecraftXRange"), Parameters.parameters.integerParameter("minecraftYRange"), Parameters.parameters.integerParameter("minecraftZRange")};
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

	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		// Checking one should be sufficient, but check all just in case
		return ((Double) behaviorMap.get("WidthFitness")).doubleValue() == 0 ||
			   ((Double) behaviorMap.get("HeightFitness")).doubleValue() == 0 ||
			   ((Double) behaviorMap.get("DepthFitness")).doubleValue() == 0;
	}
}
