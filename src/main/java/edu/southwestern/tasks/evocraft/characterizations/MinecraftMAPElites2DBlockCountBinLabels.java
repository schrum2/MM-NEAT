package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;

/**
 * Generalized class that extends MinecraftMAPElitesBinLabels that can take in block types and compare the counts
 * @author raffertyt
 *
 */
public abstract class MinecraftMAPElites2DBlockCountBinLabels extends MinecraftMAPElitesBinLabels{

	private List<String> labels = null;
	private List<MinecraftFitnessFunction> properties;
	
	/**
	 * Constructor for changing your block types
	 * @param newProperties
	 */
	public MinecraftMAPElites2DBlockCountBinLabels(List<MinecraftFitnessFunction> newProperties) {
		properties = newProperties;
	}

	@Override
	public int numberOfBins() {
		int sizeBlockCount = possibleBlockCounts();
		return sizeBlockCount*sizeBlockCount;
	}
	
	@Override
	public List<String> binLabels() {
		if(labels == null) { 
			int sizeBlockCount = possibleBlockCounts();
			labels = new ArrayList<String>(sizeBlockCount*sizeBlockCount);

			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities , -1 for negative space(j < size would just give a range of 0-999)
			for(int i = 0; i < sizeBlockCount; i++) {
				for(int j = 0; j < sizeBlockCount; j++)
				labels.add(properties.get(0).getClass().getSimpleName()+i+"_"+properties.get(1).getClass().getSimpleName()+j); 
			}
		}
		return labels;
	}

	/**
	 * Extracted method so you only have to get possible block counts once
	 * @return int volume of evolved shape
	 */
	private int possibleBlockCounts() {
		int xDim = Parameters.parameters.integerParameter("minecraftXRange");
		int yDim = Parameters.parameters.integerParameter("minecraftYRange");
		int zDim = Parameters.parameters.integerParameter("minecraftZRange");
		
		int sizeBlockCount = xDim*yDim*zDim+1; // Right size for having 0's
		return sizeBlockCount;
	}

	@Override
	public int oneDimensionalIndex(int[] multi) {
		multi[1]++; // Needs to be done so no negative indexes
		
		int binIndex = (multi[0])*dimensionSizes()[1] + multi[1];	
		//System.out.println("multi 0:"+multi[0]+"  dimSize:"+dimensionSizes()[1]+"  multi[1]:"+multi[1]+"  bin"+binIndex);
		return binIndex;
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
}
