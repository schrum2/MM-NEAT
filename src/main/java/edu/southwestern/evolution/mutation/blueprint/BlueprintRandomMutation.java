package edu.southwestern.evolution.mutation.blueprint;

import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.genotypes.NumericArrayGenotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.experiment.evolution.MultiplePopulationGenerationalEAExperiment;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BlueprintRandomMutation extends Mutation<ArrayList<Long>> {

	protected final double rate;

	public BlueprintRandomMutation() {
		this.rate = Parameters.parameters.doubleParameter("blueprintRandomRate");
	}

	/**
	 * Always perform mutation, but have per-index-rate
	 *
	 * @return
	 */
	@Override
	public boolean perform() {
		return true;
	}

	@Override
	public void mutate(Genotype<ArrayList<Long>> genotype) {
		NumericArrayGenotype<Long> ng = (NumericArrayGenotype<Long>) genotype;
		for (int i = 0; i < ng.getPhenotype().size(); i++) {
			if (RandomNumbers.randomGenerator.nextDouble() <= rate) {
				mutateIndex(ng, i);
			}
		}
	}

	/**
	 * Switches a single genotype id in the blueprint to be the genotype id of a
	 * random member of appropriate subpopulation. Only makes sense in the
	 * context of cooperative coevolution, which is why MONE.experiment must be
	 * an MultiplePopulationGenerationalEAExperiment.
	 *
	 * @param genotype
	 *            blueprint genotype
	 * @param i
	 *            index in blueprint to modify
	 */
	public void mutateIndex(NumericArrayGenotype<Long> genotype, int i) {
		assert(MMNEAT.experiment instanceof MultiplePopulationGenerationalEAExperiment);
		MultiplePopulationGenerationalEAExperiment exp = (MultiplePopulationGenerationalEAExperiment) MMNEAT.experiment;
		long newId = exp.randomIdFromSubpop(i);
		genotype.getPhenotype().set(i, newId);
	}
}
