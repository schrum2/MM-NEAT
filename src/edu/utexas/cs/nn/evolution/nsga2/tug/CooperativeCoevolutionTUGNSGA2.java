package edu.utexas.cs.nn.evolution.nsga2.tug;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.nsga2.CooperativeCoevolutionNSGA2;
import edu.utexas.cs.nn.scores.Score;
import java.util.ArrayList;

/**
 *
 * @author Jacob
 */
@SuppressWarnings("rawtypes") // to allow for different types of populations
public class CooperativeCoevolutionTUGNSGA2 extends CooperativeCoevolutionNSGA2 {

	private final TUGNSGA2 tug;

	public CooperativeCoevolutionTUGNSGA2() {
		this.tug = new TUGNSGA2(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation) {
		if (popIndex == 0) {
			// Use TUG
			ArrayList<Genotype> result = tug.selection(toKeep, sourcePopulation);
			tug.generation++;
			return result;
		} else {
			return super.selection(popIndex, toKeep, sourcePopulation);
		}
	}

	@Override
	public void close(ArrayList<ArrayList<Genotype>> populations) {
		super.close(populations);
		if (writeOutput) {
			tug.tugLog.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Score> prepareSourcePopulation(int popIndex, ArrayList<Score> parentScores, ArrayList<Score> childrenScores, int mltype) {
		if (popIndex == 0) {
			// TUG log management
			double[][] stats = tug.performanceAndGoalTargets(parentScores);
			double[] averages = stats[0];
			double[] maxes = stats[1];
			tug.adjustGoalsAndObjectives(averages, maxes);
			tug.logOut(averages, maxes);
		}
		return super.prepareSourcePopulation(popIndex, parentScores, childrenScores, mltype);
	}
}
