package edu.southwestern.evolution.nsga2;

import java.util.ArrayList;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mulambda.CoevolutionMuLambda;
import edu.southwestern.scores.Score;
import edu.southwestern.util.PopulationUtil;

/**
 * This is an evolutionary algorithm for use with cooperative coevolution,
 * but it uses the selection mechanism of NSGA2. In fact, selection is the
 * only method from CoevolutionMuLambda that is overridden, and
 * it only differs from standard NSGA2 in that some annoying type manipulation
 * operations need to be performed to allow for the possibility of each
 * coevolved sub-population being of a different type.
 *
 * @author Jacob Schrum
 */
public class CoevolutionNSGA2 extends CoevolutionMuLambda {

	/**
	 * Performs standard NSGA2 selection on one population, but strips
	 * the phenotype information to allow for general applicability to
	 * populations of any type.
         * @return selected genotypes from one population
	 */
	@SuppressWarnings("rawtypes") // Each population can be a different type
	@Override
	public ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation) {
		return PopulationUtil.removeListGenotypeType(NSGA2.staticSelection(toKeep, NSGA2.staticNSGA2Scores(PopulationUtil.addListScoreType(sourcePopulation))));
	}
}
