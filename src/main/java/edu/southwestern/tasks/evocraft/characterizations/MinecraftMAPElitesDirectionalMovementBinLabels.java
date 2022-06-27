package edu.southwestern.tasks.evocraft.characterizations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.evocraft.MinecraftClient.MinecraftCoordinates;
import edu.southwestern.tasks.evocraft.MinecraftUtilClass;
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
		
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		
		int xBinPlacement = binPlacement(ranges.x(), xMovement);
		int yBinPlacement = binPlacement(ranges.y(), yMovement);;
		int zBinPlacement = binPlacement(ranges.z(), zMovement);;
		
		return null;
	}

	private int binPlacement(int coordinate, double movement) {
		double halfRange = (coordinate + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")) /2.0;
		double binOneDimension = halfRange/Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
		
		double distanceFromEdge = halfRange + movement;
		
		int binNumber = 0;
		
		if(movement < 0) {
			if(distanceFromEdge < 0) {
				binNumber = 0;
			} else if(distanceFromEdge > 0 && distanceFromEdge < halfRange) {
				binNumber = (int) (distanceFromEdge/Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement"));
			} else {
				assert distanceFromEdge == 0;
				binNumber = Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
			}
		} else {
			assert movement >= 0;
			if(distanceFromEdge > 2*Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement")) {
				binNumber = 2*Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
			} else if(distanceFromEdge > 0 && distanceFromEdge < 2*Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement")) {
				binNumber = (int) (distanceFromEdge/Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement"));
			} else {
				assert distanceFromEdge == 0;
				binNumber = Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
			}
		}
		return binNumber;
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
		MinecraftCoordinates reservedSpace = MinecraftUtilClass.reservedSpace();
		int numberOfBinIntervals = Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
		return new int[] {(reservedSpace.x()/2)/numberOfBinIntervals,(reservedSpace.y()/2)/numberOfBinIntervals,(reservedSpace.z()/2)/numberOfBinIntervals};
	}

	@Override
	public List<MinecraftFitnessFunction> properties() {
		// Return an empty list since the properties for this approach are not derived from fitness functions
		return new ArrayList<>(0);
	}

}
