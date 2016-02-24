package edu.utexas.cs.nn.evolution.mutation.real;

import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;

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
        //genotype.getPhenotype().set(i, genotype.getPhenotype().get(i) + (magnitude[i] * RandomNumbers.fullSmallRand()));
        genotype.getPhenotype().set(i, genotype.getPhenotype().get(i) + (magnitude[i] * MMNEAT.weightPerturber.randomOutput()));
    }
}
