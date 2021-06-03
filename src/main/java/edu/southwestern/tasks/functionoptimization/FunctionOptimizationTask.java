package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.MAPElites;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
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
		double score = -function.valueOf(vector); // Must be negated to work since CMA-ES is a minimizer
		double[] scores = new double[] {score}; 
		Score<ArrayList<Double>> result =  new Score<>(individual, scores, null);
		
		if(MMNEAT.usingDiversityBinningScheme) {
			int dim1, dim2;
			if(MMNEAT.getArchiveBinLabelsClass() instanceof FunctionOptimizationRastriginBinLabels) {
				FunctionOptimizationRastriginBinLabels labels = (FunctionOptimizationRastriginBinLabels) MMNEAT.getArchiveBinLabelsClass();
				double[] characteristic = labels.behaviorCharacterization(vector);
				int[] dimensions = labels.discretize(characteristic);
				dim1 = dimensions[0];
				dim2 = dimensions[1];			
			} else {
				throw new RuntimeException("A Valid Binning Scheme For Mario Was Not Specified");
			}
			// Row-major order lookup in 2D archive
			oneMAPEliteBinIndexScorePair = new Pair<int[], Double>(new int[] {dim1, dim2}, -score);
		}		
		
		if(MMNEAT.usingDiversityBinningScheme)
			result.assignMAPElitesBinAndScore(oneMAPEliteBinIndexScorePair.t1, oneMAPEliteBinIndexScorePair.t2);
		
		return result;
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Test with Rosenbrock, comparable to results from CMExample1
		int runNum = 6;
		MMNEAT.main(new String[] {"runNumber:"+runNum, "randomSeed:"+runNum, "io:true", "base:functionoptimization", "log:fo-FunctionOptimization", "saveTo:FunctionOptimization", "netio:false", "ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA", "watch:true", "task:edu.southwestern.tasks.functionoptimization.FunctionOptimizationTask",
				"foFunction:fr.inria.optimization.cmaes.fitness.RosenFunction", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype", "foVectorLength:10", "foUpperBounds:5", "foLowerBounds:-5"});
	}

}
