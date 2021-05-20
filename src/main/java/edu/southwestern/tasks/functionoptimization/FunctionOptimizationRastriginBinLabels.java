package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.BinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Rastrigin function, based on the description from
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRastriginBinLabels implements BinLabels {

	List<String> labels = null;
	private static int BINS_PER_DIMENSION;
	private int solutionVectorLength;
	private static final double RASTRIGIN_RANGE = 5.12;

	public FunctionOptimizationRastriginBinLabels() {
		BINS_PER_DIMENSION = Parameters.parameters.integerParameter("foBinDimension");
		solutionVectorLength = Parameters.parameters.integerParameter("foVectorLength");
	}

	/**
	 * Creates bin labels on the first run through, 
	 * and always returns them
	 * 
	 * @return List of strings of bin labels
	 */
	@Override
	public List<String> binLabels() {
		if (labels == null) {
			double b = RASTRIGIN_RANGE * solutionVectorLength / BINS_PER_DIMENSION; // calculate bin size
			int size = BINS_PER_DIMENSION * BINS_PER_DIMENSION;
			labels = new ArrayList<String>(size);
			for (int y = (BINS_PER_DIMENSION / 2) - 1; y >= -(BINS_PER_DIMENSION / 2); y--) {
				for (int x = -(BINS_PER_DIMENSION / 2); x < (BINS_PER_DIMENSION / 2); x++) {
					labels.add("bin(" + x + ", " + y + ")[" + x * b + " to " + (x + 1) * b + "][" + y * b + " to "
							+ (y + 1) * b + "]");
					// bin(-250, 249)[-51.2 to -50.9952][50.9952 to 51.2] first element <--
					// bin(0, 0)[0 to 0.2048][0 to 0.2048] middle element <-- For a dimension of 500
					// bin(249, -250)[50.9952 to 51.2][-51.2 to -50.9952] last element <--
				}
			}
		}
		return labels;
	}

	/**
	 * Converts coordinates into a one-dimensional index 
	 * Expects values from -250 to 249 for the x and y 
	 * coordinates
	 * 
	 * @return A converted 1D index
	 */
	@Override
	public int oneDimensionalIndex(int[] multi) {
		return (multi[0] + BINS_PER_DIMENSION / 2) + BINS_PER_DIMENSION * (-multi[1] + (BINS_PER_DIMENSION / 2 - 1));
	}

	/**
	 * Restricts the contribution of a value to within the typical rastrigin range.
	 * If within the range, just return, otherwise divide the range by the value and
	 * return.
	 * 
	 * @param x A value to be clipped
	 * @return The clipped value
	 */
	public static double clip(double x) {
		if (x >= -RASTRIGIN_RANGE && x <= RASTRIGIN_RANGE) {
			return x; // if already in range just return
		} else {
			return RASTRIGIN_RANGE / x; // otherwise divide range by x and return
		}
//		if (x > RASTRIGIN_RANGE) {			// This is a normal clamp, however since
//			return RASTRIGIN_RANGE;			// the reference paper:
//		} else if (x < -RASTRIGIN_RANGE) {	// (https://arxiv.org/pdf/1912.02400.pdf) 
//			return -RASTRIGIN_RANGE;		// did it the other way, that's how we're
//		} else {							// going to do it too.
//			return x;
//		}
	}

	/**
	 * Gets the behavior characterization of a solution using the rastrigin
	 * characterization described in https://arxiv.org/pdf/1912.02400.pdf
	 * 
	 * @param solution
	 * @return
	 */
	public double[] behaviorCharacterization(double[] solution) {
		double[] sums = new double[] { 0, 0 }; // create array for sums
		for (int i = 0; i < solutionVectorLength / 2; i++) {
			sums[0] += clip(solution[i]); // sum first half
		}
		for (int i = solutionVectorLength / 2; i < solutionVectorLength; i++) {
			sums[1] += clip(solution[i]); // sum second half
		}
		return sums;
	}

	/**
	 * Discretizes given values into coordinates of where a bin is located.
	 * 
	 * @param behaviorCharacterization Two double values (x and y) to be discretized
	 * @return Coordinates of a bin given the coordinates
	 */
	public int[] discretize(double[] behaviorCharacterization) {
		double x_dim = behaviorCharacterization[0];
		double y_dim = behaviorCharacterization[1];
		double scalar = BINS_PER_DIMENSION / (RASTRIGIN_RANGE * solutionVectorLength); // get scalar to multiply values
																						// by to get coordinates
		return new int[] { (int) Math.floor(x_dim * scalar), (int) Math.floor(y_dim * scalar) };
	}

	// Test CMA-ME with Rastrigin function
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		int runNum = 100;
		MMNEAT.main(("runNumber:" + runNum + " randomSeed:" + runNum + " io:true base:cmamefunctionoptimization log:cmamefunctionoptimization-CMAMEFunctionOptimization saveTo:CMAMEFunctionOptimization netio:false maxGens:50000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
	}
}
