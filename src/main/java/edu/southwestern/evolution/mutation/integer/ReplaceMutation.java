package edu.southwestern.evolution.mutation.integer;

import edu.southwestern.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.southwestern.evolution.genotypes.Genotype;
import edu.southwestern.evolution.mutation.Mutation;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;
import java.util.ArrayList;

/**
 * Replace integer with new random integer
 *
 * @author Jacob Schrum
 */
public class ReplaceMutation extends Mutation<ArrayList<Integer>> {

	protected final double rate;

	public ReplaceMutation() {
		this.rate = Parameters.parameters.doubleParameter("intReplaceRate");
	}

	/*
	 * Each index is checked to see if mutation should be performed
	 */
	@Override
	public boolean perform() {
		return RandomNumbers.randomGenerator.nextDouble() <= rate;
	}

	@Override
	public void mutate(Genotype<ArrayList<Integer>> genotype) {
		for (int i = 0; i < genotype.getPhenotype().size(); i++) {
			if (perform()) {
				mutateIndex((BoundedIntegerValuedGenotype) genotype, i);
			}
		}
	}

	public void mutateIndex(BoundedIntegerValuedGenotype genotype, int i) {
		genotype.getPhenotype().set(i, RandomNumbers.randomGenerator.nextInt(MMNEAT.discreteCeilings[i]));
	}
}
