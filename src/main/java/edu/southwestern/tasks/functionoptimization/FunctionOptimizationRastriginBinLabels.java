package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;

/**
 * Binning scheme for Rastrigin function,
 * based on the description from
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRastriginBinLabels implements BinLabels {
	
	List<String> labels = null;
	private static final int BINS_PER_DIMENSION = 500;
	private static final double RASTRIGIN_RANGE = 5.12;
	private int n = 20; // should be based off of the genome length instead of hardcoded
	
	/**
	 * Creates bin labels on the first run 
	 * through, and returns them
	 * 
	 * @return List of strings of bin labels
	 */
	@Override
	public List<String> binLabels() {
		if(labels == null) {
			double b = RASTRIGIN_RANGE*n/BINS_PER_DIMENSION; // Calculate bin size
			int size = BINS_PER_DIMENSION*BINS_PER_DIMENSION;
			labels = new ArrayList<String>(size);
			for (int y = (BINS_PER_DIMENSION/2)-1; y >= -(BINS_PER_DIMENSION/2); y--) {
				for (int x = -(BINS_PER_DIMENSION/2); x < (BINS_PER_DIMENSION/2); x++) {
					labels.add("bin("+x+", "+y+")["+x*b+" to "+(x+1)*b+"]["+y*b+" to "+(y+1)*b+"]");
					// bin(-250, 249)[-51.2 to -50.9952][50.9952 to 51.2] first element
					// bin(0, 0)[0 to 0.2048][0 to 0.2048]				  middle element
					// bin(249, -250)[50.9952 to 51.2][-51.2 to -50.9952] last element
				}
			}
		}
		return labels;
	}

	/**
	 * Converts coordinates into a 
	 * one-dimensional index
	 * Expects values from -250 to 249 
	 * for the x and y coordinates
	 * 
	 * @return A converted 1D index
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		return (multi[0]+BINS_PER_DIMENSION/2) + BINS_PER_DIMENSION*(-multi[1]+(BINS_PER_DIMENSION/2-1));
	}
	
	/**
	 * Restricts the contribution of a
	 * value to within the typical 
	 * rastrigin range.
	 * If within the range, just 
	 * return, otherwise divide the 
	 * range by the value and return.
	 * 
	 * @param x A value to be clipped
	 * @return The clipped value
	 */
	public static double clip(double x) {
		if (x >= -RASTRIGIN_RANGE && x <= RASTRIGIN_RANGE) { 
			return x; // if already in range just return
		} else {
			return RASTRIGIN_RANGE/x; // otherwise divide range by x and return
		}
	}
	
	/**
	 * Gets the behavior characterization
	 * of a solution using the rastrigin 
	 * characterization described in
	 * https://arxiv.org/pdf/1912.02400.pdf
	 * 
	 * @param solution
	 * @return
	 */
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[1]; // create array for sums
		for (int i = 0; i < n/2; i++) { 
			sums[0] += clip(solution[i]); // sum first half
		}
		for (int i = n/2; i < n; i++) {
			sums[1] += clip(solution[i]); // sum second half
		}
		return sums;
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		MMNEAT.main("runNumber:0 randomSeed:0 io:true base:cmamefunctionoptimization log:cmamefunctionoptimization-CMAMEFunctionOptimization saveTo:CMAMEFunctionOptimization netio:false ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimization foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels".split(" "));
	}
}
