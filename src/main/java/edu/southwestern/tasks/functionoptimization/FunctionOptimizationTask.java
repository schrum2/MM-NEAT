package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import cern.colt.Arrays;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.MiscUtil;
import edu.southwestern.util.datastructures.ArrayUtil;
import edu.southwestern.util.datastructures.Pair;
import fr.inria.optimization.cmaes.fitness.AbstractObjectiveFunction;

/**
 * Function Optimization, primarily meant as a test class
 * for testing the functionality of CMA-ES, and to be 
 * compared with the default CMA-ES examples.
 *
 * @author Maxx Batterton
 */
public class FunctionOptimizationTask extends LonerTask<ArrayList<Double>> {

	AbstractObjectiveFunction function;
	private Pair<int[],Double> oneMAPEliteBinIndexScorePair;
	
	/**
	 * Initializes with the function specified by the 
	 * command line parameter "foFunction", and registers
	 * the function.
	 */
	public FunctionOptimizationTask() {
		try {
			this.function = (AbstractObjectiveFunction) ClassCreation.createObject("foFunction");
		} catch (NoSuchMethodException e) {
			System.out.println("Could not initalize function for FunctionOptimization");
			e.printStackTrace();
			System.exit(1);
		}
		MMNEAT.registerFitnessFunction(function.getClass().getSimpleName());
	}
	
	@Override
	public int numObjectives() { // only one function
		return 1; 
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}
	
	/**
	 * Evaluates an individual with the provided function.
	 * Must negate the result due to the fact that
	 * CMA-ES is a minimizer.
	 * 
	 * @return A Score that contains only one score, evaluated by the single function
	 */
	@Override
	public Score<ArrayList<Double>> evaluate(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> pheno = individual.getPhenotype();
		double[] vector = ArrayUtil.doubleArrayFromList(pheno); // Convert ArrayList into double array to give to function
		//vector = ArrayUtil.doubleSpecified(20, 3.0);
		double score = -function.valueOf(vector); // Must be negated to work since CMA-ES is a minimizer
		double[] scores = new double[] {score}; 
		Score<ArrayList<Double>> result =  new Score<>(individual, scores, null);
		
		if(MMNEAT.usingDiversityBinningScheme) {
			int[] dimensions;
			if(MMNEAT.getArchiveBinLabelsClass() instanceof FunctionOptimizationRangeBinLabels) {
				FunctionOptimizationRangeBinLabels labels = (FunctionOptimizationRangeBinLabels) MMNEAT.getArchiveBinLabelsClass();
				double[] characteristic = labels.behaviorCharacterization(vector);
				dimensions = labels.discretize(characteristic);		
//				for (int dim : dimensions) {
//					if (dim > 375 || dim < 100) {
//						System.out.println("\nvector: "+Arrays.toString(vector));
//						System.out.println("score: "+score);
//						System.out.println("characteristic: "+Arrays.toString(characteristic));
//						System.out.println("dimensions: "+Arrays.toString(dimensions));
//						MiscUtil.waitForReadStringAndEnterKeyPress();
//					}
//				}
			} else {
				throw new RuntimeException("A Valid Binning Scheme For Function Optimization Was Not Specified");
			}
			// Row-major order lookup in 2D archive
			oneMAPEliteBinIndexScorePair = new Pair<int[], Double>(dimensions, score);
		}		
		
		if(MMNEAT.usingDiversityBinningScheme)
			result.assignMAPElitesBinAndScore(oneMAPEliteBinIndexScorePair.t1, oneMAPEliteBinIndexScorePair.t2);
		
		//System.out.println("Bin and Score: "+Arrays.toString(oneMAPEliteBinIndexScorePair.t1) + oneMAPEliteBinIndexScorePair.t2);
		return result;
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		int runNum = 0;
		// Test with MAP Elites and sphere function
		MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" io:true base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-MAPElitesTEST saveTo:MAPElitesTEST netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.MAPElites task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction steadyStateIndividualsPerGeneration:500 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12 mapElitesQDBaseOffset:525").split(" "));
		
		// Test with CMA-ME and sphere function
		//MMNEAT.main(("runNumber:"+runNum+" randomSeed:"+runNum+" polynomialMutation:false numImprovementEmitters:15 numOptimizingEmitters:0 io:true base:mapelitesfunctionoptimization log:mapelitesfunctionoptimization-CMAMETEST saveTo:CMAMETEST netio:false maxGens:10000 ea:edu.southwestern.evolution.mapelites.CMAME task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask foFunction:fr.inria.optimization.cmaes.fitness.RastriginFunction lambda:37 steadyStateIndividualsPerGeneration:500 genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype experiment:edu.southwestern.experiment.evolution.SteadyStateExperiment mapElitesBinLabels:edu.southwestern.tasks.functionoptimization.FunctionOptimizationRangeBinLabels foBinDimension:500 foVectorLength:20 foUpperBounds:5.12 foLowerBounds:-5.12 mapElitesQDBaseOffset:525").split(" "));
		
		// Test with Rosenbrock, comparable to results from CMExample1
		//MMNEAT.main(new String[] {"runNumber:"+runNum, "randomSeed:"+runNum, "io:true", "base:functionoptimization", "log:fo-FunctionOptimization", "saveTo:FunctionOptimization", "netio:false", "ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA", "watch:true", "task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask",
		//		"foFunction:fr.inria.optimization.cmaes.fitness.RosenFunction", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "foVectorLength:10", "foUpperBounds:5", "foLowerBounds:-5"});
	}
}
