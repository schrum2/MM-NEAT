package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.util.datastructures.ArrayUtil;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class ImprovementEmitter extends Emitter {

	public ImprovementEmitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		super(dimension, archive, id);
	}
	
	@Override
	protected String getEmitterSuffix() {
		return "Improvement";
	}

	@Override
	protected CMAEvolutionStrategy newCMAESInstance(Archive<ArrayList<Double>> archive) {
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		Genotype<ArrayList<Double>> elite = archive.getElite(archive.randomOccupiedBinIndex()).individual;
		double[] phenod = ArrayUtil.doubleArrayFromList(elite.getPhenotype());
		optEmitter.setInitialX(phenod); // start at random bin
		optEmitter.setInitialStandardDeviation(0.5); // unsure if should be hardcoded or not
		int lambda = Parameters.parameters.integerParameter("lambda"); 
		// Realized that mu = lambda / 2 after extensively reviewing the pyribs code and walking through with Amy Hoover.
		// Even in pyribs, there is apparently an option for mu to be set differently, but it is definitely fixed, and
		// the default is for it to be half of lambda.
		int mu = lambda/2; 
		optEmitter.parameters.setMu(mu);
		optEmitter.parameters.setPopulationSize(lambda);
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return optEmitter;
	}
	
	@Override
	public double calculateFitness(double newScore, double currentScore) {
		if (currentScore >= newScore) { // if bin was better or equal: Remember that CMA-ES is a minimizer
			if (CMAME.PRINT_DEBUG) {System.out.println("Current bin ("+currentScore+") was already better than or equal to new bin ("+newScore+").");}
		} else {
			solutionCount++;
			validParents++;
			if (Double.isInfinite(currentScore)) { // if bin was empty (infinite magnitude must be negative infinity)
				if (CMAME.PRINT_DEBUG) {System.out.println("Added new bin ("+newScore+").");}
				return -newScore; // Negate score because CMA-ES is a minimizer	
			} else { // if bin existed, but was worse than the new one
				if (CMAME.PRINT_DEBUG) {System.out.println("Improved current bin ("+currentScore+") with new bin ("+newScore+")");}
			}
		}	
		return -(newScore - currentScore); // Negate difference score because CMA-ES is a minimizer	
	}	
}
