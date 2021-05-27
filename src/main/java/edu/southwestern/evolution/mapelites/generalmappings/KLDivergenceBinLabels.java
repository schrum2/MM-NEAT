package edu.southwestern.evolution.mapelites.generalmappings;

import distance.convolution.ConvNTuple;
import distance.kl.KLDiv;
import distance.test.KLDivTest;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for KL Divergence
 * 
 * @author Maxx Batterton
 *
 */
public class KLDivergenceBinLabels extends MultiDimensionalRealValuedBinLabels {
	
	public KLDivergenceBinLabels() {
		super(Parameters.parameters.integerParameter("klDivBinDimension"), 0, Parameters.parameters.doubleParameter("klDivMaxValue"), 2, 1);
	}
	
	/**
	 * Get the behaviorCharacterization of a provided level
	 * with the task specified  
	 * 
	 * @param levels
	 * @return
	 */
	public static double[] behaviorCharacterization(int[][] solutionLevel, int[][][] dimensionLevels) {
		double[] bc = new double[dimensionLevels.length];
		for (int i = 0; i < dimensionLevels.length; i++) {
			bc[i] = getKLDivergence(solutionLevel, dimensionLevels[i]);
		}
		return bc;
	}
	
	/**
	 * Calculate the KL Divergence of two provided levels
	 * 
	 * @param level1 The first level to compare
	 * @param level2 The second level to compare
	 * @return A double representing the KL divergence
	 */
	public static double getKLDivergence(int[][] level1, int[][] level2) {
		ConvNTuple c1 = KLDivTest.getConvNTuple(level1, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));
		ConvNTuple c2 = KLDivTest.getConvNTuple(level2, Parameters.parameters.integerParameter("receptiveFieldWidth"), Parameters.parameters.integerParameter("receptiveFieldHeight"), Parameters.parameters.integerParameter("stride"));

		double klDiv = KLDiv.klDivSymmetric(c1.sampleDis, c2.sampleDis);
		
		return klDiv;
	}
	
	// test something here
	public static void main(String[] args) {
	}
	
}
