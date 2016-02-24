package edu.utexas.cs.nn.evolution.genotypes;

import edu.utexas.cs.nn.evolution.mutation.real.PerturbMutation;
import edu.utexas.cs.nn.evolution.mutation.real.PolynomialMutation;
import edu.utexas.cs.nn.util.random.RandomNumbers;
import edu.utexas.cs.nn.parameters.Parameters;
import edu.utexas.cs.nn.MMNEAT.MMNEAT;
import edu.utexas.cs.nn.util.datastructures.ArrayUtil;
import java.util.ArrayList;

/**
 *
 * @author Jacob Schrum
 */
public class BoundedRealValuedGenotype extends RealValuedGenotype {

    double[] lower;
    double[] upper;
    boolean polynomialMutation;

    public BoundedRealValuedGenotype() {
        // May need to change this if other tasks start using the real-coded genotype
        this(MMNEAT.fos.getLowerBounds(), MMNEAT.fos.getUpperBounds());
    }

    public BoundedRealValuedGenotype(double[] lower, double[] upper) {
        this(RandomNumbers.randomBoundedArray(lower, upper), lower, upper);
    }

    private BoundedRealValuedGenotype(double[] genes, double[] lower, double[] upper) {
        super(genes);
        polynomialMutation = Parameters.parameters.booleanParameter("polynomialMutation");

        this.lower = lower;
        this.upper = upper;
        bound();
    }

    public BoundedRealValuedGenotype(ArrayList<Double> genes, double[] lower, double[] upper) {
        this(ArrayUtil.doubleArrayFromList(genes), lower, upper);
    }

    private BoundedRealValuedGenotype(RealValuedGenotype genotype, double[] lower, double[] upper) {
        this(genotype.genes, lower, upper);
    }

    @Override
    public Genotype<ArrayList<Double>> copy() {
        double[] array = new double[genes.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = genes.get(i);
        }
        return new BoundedRealValuedGenotype(array, lower, upper);
    }

    public final double[] getRange() {
        double[] magnitudes = new double[lower.length];
        for (int i = 0; i < magnitudes.length; i++) {
            magnitudes[i] = upper[i] - lower[i];
        }
        return magnitudes;
    }

    @Override
    public void mutate() {
        if (polynomialMutation) {
            new PolynomialMutation().mutate(this);
        } else {
            new PerturbMutation(getRange()).mutate(this);
        }

        bound();
    }

    public final void bound() {
        for (int i = 0; i < genes.size(); i++) {
            double x = genes.get(i);
            if (x < lower[i]) {
                x = lower[i];
            } else if (x > upper[i]) {
                x = upper[i];
            }
            genes.set(i, x);
        }
    }

    @Override
    public Genotype<ArrayList<Double>> newInstance() {
        return new BoundedRealValuedGenotype(lower, upper);
    }

    public double[] lowerBounds() {
        return lower;
    }

    public double[] upperBounds() {
        return upper;
    }
}
