package edu.southwestern.evolution.mapelites.emitters;

import java.util.ArrayList;

import edu.southwestern.evolution.mapelites.Archive;
import edu.southwestern.evolution.mapelites.CMAME;
import edu.southwestern.util.datastructures.Pair;

public class ImprovementEmitter extends Emitter {

	public ImprovementEmitter(int dimension, Archive<ArrayList<Double>> archive, int id) {
		super(dimension, archive, id);
	}
	
	@Override
	protected String getEmitterSuffix() {
		return "Improvement";
	}
	
	@Override
	public Pair<Double,SOLUTION_TYPE> calculateFitness(double newScore, double currentScore) {
		if (currentScore >= newScore) { // if bin was better or equal: Remember that CMA-ES is a minimizer
			if (CMAME.PRINT_DEBUG) {System.out.println("Current bin ("+currentScore+") was already better than or equal to new bin ("+newScore+").");}
			return new Pair<Double,SOLUTION_TYPE>(-(newScore - currentScore), Emitter.SOLUTION_TYPE.FAILURE);
		} else {
			solutionCount++;
			validParents++;
			if (Double.isInfinite(currentScore)) { // if bin was empty (infinite magnitude must be negative infinity)
				if (CMAME.PRINT_DEBUG) {System.out.println("Added new bin ("+newScore+").");}
				return new Pair<Double,SOLUTION_TYPE>(-newScore, Emitter.SOLUTION_TYPE.NEW_BIN); // Negate score because CMA-ES is a minimizer	
			} else { // if bin existed, but was worse than the new one
				if (CMAME.PRINT_DEBUG) {System.out.println("Improved current bin ("+currentScore+") with new bin ("+newScore+")");}
				return new Pair<Double,SOLUTION_TYPE>(-(newScore - currentScore), Emitter.SOLUTION_TYPE.IMPROVED_BIN);
			}
		}	
	}	
}
