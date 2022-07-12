package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
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
			dim = Parameters.parameters.integerParameter("minecraftPistonLabelSize");
			int size = dim * dim * dim; // size is the total possible volume
			
			labels = new ArrayList<String>(size+1);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int xi = 0; xi <dim; xi++) {
				for(int yi = 0; yi < dim; yi++) {
					for(int zi = 0; zi < dim; zi++) {
						labels.add("NS"+xi+"UD"+yi+"EW"+zi);
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
		//System.out.println(Arrays.toString(multi));

		multi[0] = Math.min(multi[0], dim-1);
		multi[1] = Math.min(multi[1], dim-1);
		multi[2] = Math.min(multi[2], dim-1);
		
		
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];

		assert binIndex < binLabels().size() : "Out of Bounds: " +Arrays.toString(multi) + " mapped to " + binIndex + " for dim = "+dim;
		
		return binIndex;
	}
	
	@Override
	public int[] dimensionSizes() {
		return new int[] {dim,dim,dim};
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		int[] multi = super.multiDimensionalIndices(keys);
		// If the number of pistons with a given orientation exceeds the dim, then simply reduce it to the max interval
		multi[0] = Math.min(multi[0], dim-1);
		multi[1] = Math.min(multi[1], dim-1);
		multi[2] = Math.min(multi[2], dim-1);

		assert multi[0]*dim*dim + multi[1]*dim + multi[2] < binLabels().size() : "Out of Bounds: " +Arrays.toString(multi) + " mapped to " + (multi[0]*dim*dim + multi[1]*dim + multi[2]) + " for dim = "+dim;
		
		return multi;
	}
	
	
	@Override
	public List<MinecraftFitnessFunction> properties() { return properties; }

}
