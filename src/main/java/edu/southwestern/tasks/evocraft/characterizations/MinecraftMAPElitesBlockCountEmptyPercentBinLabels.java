package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
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
			
			int sizeBlockCount = xDim*yDim*zDim+1; // size blocks count times 10 for each possible % category
			int sizeNegativeSpace = xDim*yDim*zDim-1; // Max possible 
			labels = new ArrayList<String>(sizeBlockCount*sizeNegativeSpace);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int i = 0; i < sizeBlockCount; i++) {
				for(int j = 0; j < sizeNegativeSpace; j++)
				labels.add("BlockCount"+i+"NegativeSpace"+j); 
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { // Based on 2d archive
		int zDim = Parameters.parameters.integerParameter("minecraftZRange")+1;
		int binIndex = multi[0]*zDim + multi[1];
		return binIndex;
	}

	@Override
	public int[] dimensionSizes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		// TODO Auto-generated method stub
		return null;
	}

}
