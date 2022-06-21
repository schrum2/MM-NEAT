package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NegativeSpaceCountFitness;
import edu.southwestern.tasks.evocraft.fitness.OccupiedCountFitness;

public class MinecraftMAPElitesRedstoneVSPistonNinLabels extends MinecraftMAPElitesBinLabels{

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new OccupiedCountFitness(), new NegativeSpaceCountFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { 
			int xDim = Parameters.parameters.integerParameter("minecraftXRange");
			int yDim = Parameters.parameters.integerParameter("minecraftYRange");
			int zDim = Parameters.parameters.integerParameter("minecraftZRange");
			
			int sizeBlockCount = xDim*yDim*zDim; 
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
		// TODO Auto-generated method stub
		return null;
	}

}
