/**
 * PolynomialMutation.java
 *
 * Copied from the implementation of PolynomialMutation.java by Juan J. Durillo
 * that was included with jmetal: http://jmetal.sourceforge.net/
 *
 */
package edu.utexas.cs.nn.evolution.mutation.real;

import edu.utexas.cs.nn.evolution.genotypes.BoundedRealValuedGenotype;
import edu.utexas.cs.nn.evolution.genotypes.RealValuedGenotype;
import edu.utexas.cs.nn.util.random.RandomNumbers;

/**
 *
 * @author Jacob Schrum
 */
public class PolynomialMutation extends RealMutation {

    public static final double eta_m_ = 20;

    @Override
    public void mutateIndex(RealValuedGenotype genotype, int var) {
        BoundedRealValuedGenotype g = ((BoundedRealValuedGenotype) genotype);

        double y = g.getPhenotype().get(var);
        double yl = g.lowerBounds()[var];
        double yu = g.upperBounds()[var];
        ((BoundedRealValuedGenotype) genotype).setValue(var, newValue(y, yl, yu));
    }

    public double newValue(double y, double yl, double yu) {
        y = y + delta(y, yl, yu);
        if (y < yl) {
            y = yl;
        }
        if (y > yu) {
            y = yu;
        }
        return y;
    }

    public double delta(double y, double yl, double yu) {
        double delta1 = (y - yl) / (yu - yl);
        double delta2 = (yu - y) / (yu - yl);
        double rnd = RandomNumbers.randomGenerator.nextDouble();
        double mut_pow = 1.0 / (eta_m_ + 1.0);
        double deltaq;
        if (rnd <= 0.5) {
            double xy = 1.0 - delta1;
            double val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (eta_m_ + 1.0)));
            deltaq = java.lang.Math.pow(val, mut_pow) - 1.0;
        } else {
            double xy = 1.0 - delta2;
            double val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (java.lang.Math.pow(xy, (eta_m_ + 1.0)));
            deltaq = 1.0 - (java.lang.Math.pow(val, mut_pow));
        }
        return deltaq * (yu - yl);
    }
}
