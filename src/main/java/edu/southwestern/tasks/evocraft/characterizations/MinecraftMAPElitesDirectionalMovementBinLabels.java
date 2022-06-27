package edu.southwestern.tasks.evocraft.characterizations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.tasks.evocraft.fitness.MinecraftFitnessFunction;

/**
 * Although this extends the MinecraftMAPElitesBinLabels, it does not follow the normal
 * pattern of simply aggregating several fitness functions.
 * 
 * @author schrum2
 *
 */
public class MinecraftMAPElitesDirectionalMovementBinLabels extends MinecraftMAPElitesBinLabels {

	@Override
	public List<String> binLabels() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		return null;
	}

	@Override
	public String[] dimensions() {
		return null;
	}
	
	public HashMap<String,Object> behaviorMapFromScores(double[] fitnessScores) {
		return null;
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
