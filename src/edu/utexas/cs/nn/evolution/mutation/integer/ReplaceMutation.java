package edu.utexas.cs.nn.evolution.mutation.integer;

import edu.utexas.cs.nn.evolution.genotypes.BoundedIntegerValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.mutation.Mutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
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
