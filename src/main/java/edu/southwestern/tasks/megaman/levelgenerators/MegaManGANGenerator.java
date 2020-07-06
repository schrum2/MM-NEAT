package edu.southwestern.tasks.megaman.levelgenerators;

import java.util.List;

import edu.southwestern.parameters.Parameters;

/**
 * Given variables associated with a single segment (both latent and aux),
 * generate the segment.
 * @author Jacob Schrum
 *
 */
public abstract class MegaManGANGenerator {
	
	/**
	 * Number of auxiliary variables at the start of each set of segmentVariables
	 * @return Num variables
	 */
	public static int numberOfAuxiliaryVariables() {
		return 3; // Currently only supporting Right, Up, Down, but will add Left (return 4) soon
	}
	
	public enum SEGMENT_TYPE {UP, DOWN, LEFT, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT};
	
	public List<List<Integer>> generateSegmentFromVariables(double[] segmentVariables, SEGMENT_TYPE previous){
		// Save latent vector
		double[] latentVector = new double[Parameters.parameters.integerParameter("GANInputSize")];
		System.arraycopy(segmentVariables, numberOfAuxiliaryVariables(), latentVector, 0, latentVector.length);
		// Save aux variables
		double[] auxiliaryVariables = new double[numberOfAuxiliaryVariables()];
		System.arraycopy(segmentVariables, 0, auxiliaryVariables, 0, auxiliaryVariables.length);
		
		SEGMENT_TYPE type = determineType(previous, auxiliaryVariables);
		
		return generateSegmentFromLatentVariables(latentVector, type);
	}
	
	public static SEGMENT_TYPE determineType(SEGMENT_TYPE previous, double[] auxiliaryVariables) {
		return null;
	}

	public abstract List<List<Integer>> generateSegmentFromLatentVariables(double[] latentVariables, SEGMENT_TYPE type);
}
