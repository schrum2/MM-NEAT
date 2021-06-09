package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.mapelites.generalmappings.MultiDimensionalRealValuedSlicedBinLabels;
import edu.southwestern.parameters.Parameters;

/**
 * Binning scheme for Rastrigin function, based on the description from
 * https://arxiv.org/pdf/1912.02400.pdf
 * 
 * @author Maxx Batterton
 *
 */
public class FunctionOptimizationRastriginBinLabels extends MultiDimensionalRealValuedSlicedBinLabels {

	private static final double RASTRIGIN_RANGE = 5.12;

	public FunctionOptimizationRastriginBinLabels() {
		super(Parameters.parameters.integerParameter("foBinDimension"), -RASTRIGIN_RANGE/2, RASTRIGIN_RANGE/2, MMNEAT.getLowerBounds().length/2);
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
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		int runNum = 5;
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true numImprovementEmitters:2 numOptimizingEmitters:0 solutionVectorSlices:2 base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-2 saveTo:2 netio:false lambda:37 maxGens:5000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.SphereFunction steadyStateIndividualsPerGeneration:100 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRastriginBinLabels foBinDimension:100 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12").split(" "));
	}


}
