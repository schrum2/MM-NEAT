package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NumPistonsFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;

public class MinecraftMAPElitesRedstoneVSPistonBinLabels extends MinecraftMAPElitesBinLabels{

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new NumRedstoneFitness(), new NumPistonsFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { 
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			int sizeBlockCount = xDim*yDim*zDim+1; // Right size for having 0's
			labels = new ArrayList<String>(sizeBlockCount*sizeBlockCount);

			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities , -1 for negative space(j < size would just give a range of 0-999)
			for(int i = 0; i < sizeBlockCount; i++) {
				for(int j = 0; j < sizeBlockCount; j++)
				labels.add("BlockCountRedstone"+i+"NegativeSpacePistons"+j); 
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		multi[1]++; // Needs to be done so no negative indexes
		
		int binIndex = (multi[0])*dimensionSizes()[1] + multi[1];	
		//System.out.println("multi 0:"+multi[0]+"  dimSize:"+dimensionSizes()[1]+"  multi[1]:"+multi[1]+"  bin"+binIndex);
		return binIndex;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] result = super.multiDimensionalIndices(keys);
		// Actual block count could be 0, but such shapes are discarded, only for block Count
		//result[0]--;
		return result;
	}
	
	@Override
	public int[] dimensionSizes() {
		int xtimesYtimez = Parameters.parameters.integerParameter("minecraftXRange")*Parameters.parameters.integerParameter("minecraftYRange")*Parameters.parameters.integerParameter("minecraftZRange")+1;
		return new int[] {xtimesYtimez,xtimesYtimez}; // Makes sure no empty fences are placed
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		return properties;
	}

//	@Override
//	public boolean discard(HashMap<String, Object> behaviorMap) {
//		return ((Double) behaviorMap.get("OccupiedCountFitness")).doubleValue() == 0; // IF empty, discards it (mostly first row of blockCount)
//	}
}
