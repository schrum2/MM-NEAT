package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
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
		
		double xMovement = ((Double) keys.get("x-movement")).doubleValue();
		double yMovement = ((Double) keys.get("y-movement")).doubleValue();
		double zMovement = ((Double) keys.get("z-movement")).doubleValue();
		
		
		// More to do
		
		return null;
	}

	@Override
	public String[] dimensions() {
		return new String[] {"x-movement", "y-movement", "z-movement"};
	}
	
	public HashMap<String,Object> behaviorMapFromScores(double[] fitnessScores) {
		// Since this scheme is not based on fitness functions, an empty HashMap is returned.
		// The needed scores are collected in special case particular to this binning scheme.
		return new HashMap<>();
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
		// Return an empty list since the properties for this approach are not derived from fitness functions
		return new ArrayList<>(0);
	}

}
