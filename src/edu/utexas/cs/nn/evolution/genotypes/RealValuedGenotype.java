package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.mutation.real.PerturbMutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class RealValuedGenotype extends NumericArrayGenotype<Double> {

    public RealValuedGenotype(ArrayList<Double> genes) {
        super(genes);
    }

    public RealValuedGenotype(double[] genes) {
        super(ArrayUtil.primitiveDoubleArrayToDoubleArray(genes));
    }

    public RealValuedGenotype(int size) {
        this(RandomNumbers.randomArray(size));
    }

    public Genotype<ArrayList<Double>> copy() {
        return new RealValuedGenotype(genes);
    }

    public Genotype<ArrayList<Double>> newInstance() {
        return new RealValuedGenotype(genes.size());
    }

    public void mutate() {
        new PerturbMutation(genes.size()).mutate(this);
    }
}
