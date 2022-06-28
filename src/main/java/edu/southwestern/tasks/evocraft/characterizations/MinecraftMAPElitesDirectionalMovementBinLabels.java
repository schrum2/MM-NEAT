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

	private List<String> labels = null;
	
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			int xDim = dimensionValue();
			int yDim = dimensionValue();
			int zDim = dimensionValue();
			
			int size = xDim*yDim*zDim; // size is the total possible volume
			
			labels = new ArrayList<String>(size);
			
			// go through all possible bins+1 since both 0 and 1000 blocks are both possibilities (i < size would just give a range of 0-999)
			for(int i = 1; i <= size; i++) labels.add(i + "DirectionalMovement");
		}
		return labels;
	}

	@Override
	public int[] multiDimensionalIndices(HashMap<String, Object> keys) {
		
		double xMovement = ((Double) keys.get("x-movement")).doubleValue();
		double yMovement = ((Double) keys.get("y-movement")).doubleValue();
		double zMovement = ((Double) keys.get("z-movement")).doubleValue();
		
		MinecraftCoordinates ranges = MinecraftUtilClass.getRanges();
		
		int xBinPlacement = binPlacement(ranges.x(), xMovement);
		int yBinPlacement = binPlacement(ranges.y(), yMovement);
		int zBinPlacement = binPlacement(ranges.z(), zMovement);
		
		return new int[] {xBinPlacement,yBinPlacement,zBinPlacement};
	}

	private int binPlacement(int rangeCoordinate, double movement) {
		double halfRange = (rangeCoordinate + Parameters.parameters.integerParameter("spaceBetweenMinecraftShapes")) /2.0;
		int totalNumberOfBins = dimensionValue();
		
		double distanceFromEdge = halfRange + movement;
		double binsTotalDistance = ( distanceFromEdge / (2*halfRange) ) * totalNumberOfBins;
		
		int binNumber = 0;
		
		if(movement == 0) {
			binNumber = Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement");
		} else if(binsTotalDistance < 0) {
			binNumber = 0;
		} else if(binsTotalDistance > totalNumberOfBins) {
			binNumber = totalNumberOfBins;
		} else {
			binNumber = (int) Math.floor(distanceFromEdge/(2*halfRange));
			if(binNumber >= Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement")) binNumber++; 
		}
		return binNumber;
	}
	
	private int dimensionValue() {
		return 2*Parameters.parameters.integerParameter("minecraftNumberOfBinsForMovement")+1;
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
		int yDim = dimensionValue();
		int zDim = dimensionValue();
		int binIndex = multi[0]*yDim*zDim + multi[1]*zDim + multi[2];
		return binIndex;
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
