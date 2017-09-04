package edu.southwestern.evolution.mutation.real;

import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.datastructures.ArrayUtil;

/**
 *
 * @author Jacob Schrum
 */
public class PerturbMutation extends RealMutation {

	final double[] magnitude;

	public PerturbMutation(double[] magnitude) {
		this.magnitude = magnitude;
	}

	public PerturbMutation(int size) {
		this(ArrayUtil.doubleOnes(size));
	}

	@Override
	public void mutateIndex(RealValuedGenotype genotype, int i) {
		// genotype.getPhenotype().set(i, genotype.getPhenotype().get(i) +
		// (magnitude[i] * RandomNumbers.fullSmallRand()));
		genotype.getPhenotype().set(i,
				genotype.getPhenotype().get(i) + (magnitude[i] * MMNEAT.weightPerturber.randomOutput()));
	}
}
