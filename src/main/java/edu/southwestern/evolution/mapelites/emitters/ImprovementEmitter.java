package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;
import edu.southwestern.parameters.Parameters;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;

public class ImprovementEmitter extends Emitter {

	public ImprovementEmitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		super(dimension, archive, id);
	}
	
	protected CMAEvolutionStrategy newCMAESInstance(Archive<ArrayList<Double>> archive) {
		CMAEvolutionStrategy optEmitter = new CMAEvolutionStrategy();
		optEmitter.setDimension(dimension);
		Genotype<ArrayList<Double>> elite = archive.getElite(archive.randomOccupiedBinIndex()).individual;
		Double[] phenoD = elite.getPhenotype().toArray(new Double[0]);
		double[] phenod = new double[phenoD.length];
		for (int i = 0; i < phenoD.length; i++) {
			phenod[i] = phenoD[i];
		}
		optEmitter.setInitialX(phenod); // start at random bin
		optEmitter.setInitialStandardDeviation(0.5); // TODO unsure if should be hardcoded or not
		int lambda = Parameters.parameters.integerParameter("lambda"); // TODO what's the deal with mu and lambda???
		optEmitter.parameters.setMu(lambda);
		optEmitter.parameters.setPopulationSize(lambda); // not sure about mu/lambda is mu appropriate?
		optEmitter.init();
		optEmitter.writeToDefaultFilesHeaders(0); // Overwrite existing CMA-ES files
		return optEmitter;
	}

	@Override
	protected String getEmitterSuffix() {
		return "Improvement";
	}

	@Override
	public double calculateFitness(double newScore, double currentScore) {
		if (currentScore >= newScore) { // if bin was better or equal
			if (CMAME.PRINT_DEBUG) {System.out.println("Current bin ("+currentScore+") was already better than or equal to new bin ("+newScore+").");}
			return CMAME.FAILURE_VALUE;
		} else {
			solutionCount++;
			if (Double.isInfinite(currentScore)) { // if bin was empty (infinite magnitude must be negative infinity)
				if (CMAME.PRINT_DEBUG) {System.out.println("Added new bin ("+newScore+").");}
				return -newScore; // Negate score because CMA-ES is a minimizer	
			} else { // if bin existed, but was worse than the new one
				if (CMAME.PRINT_DEBUG) {System.out.println("Improved current bin ("+currentScore+") with new bin ("+newScore+")");}
				return -(newScore - currentScore); // Negate difference score because CMA-ES is a minimizer	
			}
		}
			
	}
}
