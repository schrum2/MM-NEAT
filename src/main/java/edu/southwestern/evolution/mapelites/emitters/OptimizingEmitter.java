package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;

public class OptimizingEmitter extends Emitter {

	public OptimizingEmitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		super(dimension, archive, id);
	}

	@Override
	protected String getEmitterSuffix() {
		return "Optimizing";
	}

	@Override
	public double calculateFitness(double newScore, double currentScore) {
		if (currentScore >= newScore) { // if bin was better or equal
			if (CMAME.PRINT_DEBUG) {System.out.println("Current bin ("+currentScore+") was already better than or equal to new bin ("+newScore+").");}
		} else {
			solutionCount++;
			validParents++;
			if (Double.isInfinite(currentScore)) { // if bin was empty (infinite magnitude must be negative infinity)
				if (CMAME.PRINT_DEBUG) {System.out.println("Added new bin ("+newScore+").");}
			} else { // if bin existed, but was worse than the new one
				if (CMAME.PRINT_DEBUG) {System.out.println("Improved current bin ("+currentScore+") with new bin ("+newScore+")");}
			}
		}	
		return -newScore; // Negate score because CMA-ES is a minimizer, optimizing emitters always return the new value
	}	

}
