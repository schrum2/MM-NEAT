package edu.utexas.cs.nn.evolution.nsga2;

import java.util.ArrayList;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.mulambda.CooperativeCoevolutionMuLambda;
import edu.utexas.cs.nn.scores.Score;
import edu.utexas.cs.nn.util.PopulationUtil;

/**
 * This is an evolutionary algorithm for use with cooperative coevolution,
 * but it uses the selection mechanism of NSGA2. In fact, selection is the
 * only method from CooperativeCoevolutionMuLambda that is overridden, and
 * it only differs from standard NSGA2 in that some annoying type manipulation
 * operations need to be performed to allow for the possibility of each
 * coevolved sub-population being of a different type.
 *
 * @author Jacob Schrum
 */
public class CooperativeCoevolutionNSGA2 extends CooperativeCoevolutionMuLambda {

	/**
	 * Performs standard NSGA2 selection on one population, but strips
	 * the phenotype information to allow for general applicability to
	 * populations of any type.
	 */
	@SuppressWarnings("rawtypes") // Each population can be a different type
	@Override
	public ArrayList<Genotype> selection(int popIndex, int toKeep, ArrayList<Score> sourcePopulation) {
		return PopulationUtil.removeListGenotypeType(NSGA2.staticSelection(toKeep, NSGA2.staticNSGA2Scores(PopulationUtil.addListScoreType(sourcePopulation))));
	}
}
