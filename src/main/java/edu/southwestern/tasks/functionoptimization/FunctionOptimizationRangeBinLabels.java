package edu.southwestern.tasks.functionoptimization;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.generalmappings.MultiDimensionalRealValuedSlicedBinLabels;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.BoundedTask;

/**
 * Binning scheme for Rastrigin and Sphere function, 
 * based on the description from this paper:
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRangeBinLabels extends MultiDimensionalRealValuedSlicedBinLabels {

	private static final double RASTRIGIN_RANGE = 5.12;

	public FunctionOptimizationRangeBinLabels() {
		super(Parameters.parameters.integerParameter("foBinDimension"), -RASTRIGIN_RANGE, RASTRIGIN_RANGE, ((BoundedTask) MMNEAT.task).getLowerBounds().length);
		if (Parameters.parameters.integerParameter("solutionVectorSlices") != 2) {throw new IllegalStateException("FunctionOptimizationRastriginBinLabels can only have 2 slices!");}
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

	@Override
	protected double process(double value) {
		return clip(value);
	}
	
	@Override
	public String[] dimensions() {
		return new String[] {"First Half Sum", "Second Half Sum"};
	}
}
