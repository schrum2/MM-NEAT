package edu.southwestern.tasks.mario;

import java.util.ArrayList;
import java.util.List;

import ch.idsia.tools.EvaluationInfo;
import edu.southwestern.evolution.genotypes.Genotype;

/**
 * Used to evaluate Mario levels without actually evolving them.
 * Can pass in levels directly loaded from text file data.
 * 
 * @author Schrum
 *
 */
public class FakeMarioLevelTask extends MarioLevelTask<ArrayList<Double>> {

	@Override
	public ArrayList<List<Integer>> getMarioLevelListRepresentationFromGenotype(Genotype<ArrayList<Double>> individual) {
		// There won't be a genotype
		return null;
	}

	@Override
	public double totalPassableDistance(EvaluationInfo info) {
		double totalDistanceInLevel = info.totalLengthOfLevelPhys;
		return totalDistanceInLevel;
	}

}
