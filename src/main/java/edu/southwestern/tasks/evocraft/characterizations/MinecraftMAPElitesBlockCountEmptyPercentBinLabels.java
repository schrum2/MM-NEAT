package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NegativeSpaceCountFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;

public class MinecraftMAPElitesBlockCountEmptyPercentBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new OccupiedCountFitness(), new NegativeSpaceCountFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { 
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			// everything from block count is from OccupiedCountFitness
			int sizeBlockCount = xDim*yDim*zDim; 
			int sizeNegativeSpace = xDim*yDim*zDim-1; // Max possible negative space
			labels = new ArrayList<String>(sizeBlockCount*sizeNegativeSpace);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities , -1 for negative space(j < size would just give a range of 0-999)
			for(int i = 0; i < sizeBlockCount; i++) {
				for(int j = 0; j < sizeNegativeSpace; j++)
				labels.add("BlockCount"+i+"NegativeSpace"+j); 
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { // Based on 2d archive
		multi[1]++;
		int binIndex = (multi[0])*dimensionSizes()[1] + multi[1];
		//System.out.println("BinIndex:"+binIndex+"  multi[0]"+multi[0]+"  multi[1]"+multi[1]);
		return binIndex;
	}
	
	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] result = super.multiDimensionalIndices(keys);
		// Actual block count could be 0, but such shapes are discarded
		result[0]--;
		return result;
	}

	@Override
	public int[] dimensionSizes() {
		int xtimesYtimez = Parameters.parameters.integerParameter("minecraftXRange")*Parameters.parameters.integerParameter("minecraftYRange")*Parameters.parameters.integerParameter("minecraftZRange");
		return new int[] {xtimesYtimez,xtimesYtimez-1};
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}
	
	@Override
	public boolean discard(HashMap<String, Object> behaviorMap) {
		return ((Double) behaviorMap.get("OccupiedCountFitness")).doubleValue() == 0;
	}

}
