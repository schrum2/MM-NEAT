package edu.utexas.cs.nn.evolution.mutation.blueprint;

import edu.utexas.cs.nn.evolution.genotypes.Genotype;
import edu.utexas.cs.nn.evolution.genotypes.NumericArrayGenotype;
import edu.utexas.cs.nn.evolution.mulambda.CooperativeCoevolutionMuLambda;
import edu.utexas.cs.nn.evolution.mutation.Mutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BlueprintChildrenMutation extends Mutation<ArrayList<Long>> {

	protected final double rate;

	public BlueprintChildrenMutation() {
		this.rate = Parameters.parameters.doubleParameter("blueprintParentToChildRate");
	}

	/**
	 * Always perform the mutation, but there is a per-index-rate
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
	 * Switches a single genotype id in the blueprint to be the genotype id of
	 * one of that genotype's recent offspring. Only makes sense in the context
	 * of cooperative coevolution, which is why MONE.ea must be an instance of
	 * CooperativeCoevolutionMuLambda. It is possible the parent has not had any
	 * recent offspring, in which case no change is made.
	 *
	 * @param genotype
	 *            blueprint genotype
	 * @param i
	 *            index in blueprint to modify
	 */
	public void mutateIndex(NumericArrayGenotype<Long> genotype, int i) {
		assert(MMNEAT.ea instanceof CooperativeCoevolutionMuLambda);
		CooperativeCoevolutionMuLambda ea = (CooperativeCoevolutionMuLambda) MMNEAT.ea;
		long parentId = genotype.getPhenotype().get(i);
		Long offspringId = ea.getRandomOffspringId(parentId);
		if (offspringId != null) {
			genotype.getPhenotype().set(i, offspringId);
		}
	}
}
