package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.fitness.EastWestPistonCountFitness;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;
import edu.southwestern.tasks.evocraft.fitness.NorthSouthPistonCountFitness;
import edu.southwestern.tasks.evocraft.fitness.UpDownPistonCountFitness;

public class MinecraftMAPElitesPistonOrientationCountBinLabels extends MinecraftMAPElitesBinLabels {

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties = Arrays.asList(new NorthSouthPistonCountFitness(), new UpDownPistonCountFitness(), new EastWestPistonCountFitness());
	private int dim = Parameters.parameters.integerParameter("minecraftPistonLabelSize");
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { // Create once and re-use, but wait until after Parameters are loaded	
			int size = dim * dim * dim; // size is the total possible volume
			
			labels = new ArrayList<String>(size);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int xi = 0; xi <dim; xi++) {
				for(int yi = 0; yi < dim; yi++) {
					for(int zi = 0; zi < dim; zi++) {
						// TODO: change label once it works
						labels.add("NS"+xi+"EW"+yi+"UD"+zi);
					}
				}
			}
		}
		return labels;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) { 
		int yDim = dim;
		int zDim = dim;
		System.out.println(Arrays.toString(multi));
		
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];
		System.out.println(binIndex);
		
		return binIndex;
	}
	
	@Override
	public int[] dimensionSizes() {
		// CHANGE
		return new int[] {dim,dim,dim};
	}

	@Override
	public List<MinecraftFitnessFunction> properties() { return properties; }

}
