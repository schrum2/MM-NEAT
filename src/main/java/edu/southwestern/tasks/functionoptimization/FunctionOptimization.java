package edu.southwestern.tasks.functionoptimization;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.scores.Score;
import edu.southwestern.tasks.LonerTask;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.ArrayUtil;
import fr.inria.optimization.cmaes.fitness.AbstractObjectiveFunction;

public class FunctionOptimization extends LonerTask<ArrayList<Double>> {

	AbstractObjectiveFunction function;
	
	public FunctionOptimization() {
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
	public int numObjectives() {
		return 1;
	}

	@Override
	public double getTimeStamp() {
		return 0;
	}

	@Override
	public Score<ArrayList<Double>> evaluate(Genotype<ArrayList<Double>> individual) {
		ArrayList<Double> pheno = individual.getPhenotype();
		double[] vector = ArrayUtil.doubleArrayFromList(pheno); // Convert ArrayList into double array to give to function
		double score = -function.valueOf(vector); // Must be negated to work since CMA-ES is a minimizer
		double[] scores = new double[] {score}; 
		return new Score<>(individual, scores, null);
	}
	
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// Test with Rosenbrock, comparable to CMExample1
		MMNEAT.main(new String[] {"runNumber:1", "randomSeed:1", "io:true", "base:functionoptimization", "log:fo-FunctionOptimization", "saveTo:FunctionOptimization", "netio:false", "ea:edu.southwestern.evolution.cmaes.CMAEvolutionStrategyEA", "watch:true", "task:edu.southwestern.tasks.functionoptimization.FunctionOptimization",
				"foFunction:fr.inria.optimization.cmaes.fitness.RosenFunction", "genotype:edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype"});
	}

}
