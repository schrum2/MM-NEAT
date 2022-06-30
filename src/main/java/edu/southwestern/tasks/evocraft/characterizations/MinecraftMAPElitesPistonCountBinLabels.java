package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NorthSouthPistonCountFitness;
import edu.southwestern.tasks.evocraft.fitness.NumPistonsFitness;
import edu.southwestern.tasks.evocraft.fitness.NumRedstoneFitness;
import edu.southwestern.tasks.evocraft.fitness.PistonCountFitness;

public class MinecraftMAPElitesPistonCountBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new NorthSouthPistonCountFitness());
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
			int size = ranges.x() * ranges.y() * ranges.z(); // size is the total possible volume
			
			labels = new ArrayList<String>(size);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int i = 1; i <= size; i++) labels.add(i + "Pistons"); 
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
		return new int[] {};
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		// TODO Auto-generated method stub
		return null;
	}

}
