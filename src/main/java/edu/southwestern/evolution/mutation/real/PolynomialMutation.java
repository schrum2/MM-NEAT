/**
 * PolynomialMutation.java
 *
 * Copied from the implementation of PolynomialMutation.java by Juan J. Durillo
 * that was included with jmetal: http://jmetal.sourceforge.net/
 *
 */
package edu.southwestern.evolution.mutation.real;

import edu.southwestern.evolution.genotypes.BoundedRealValuedGenotype;
import edu.southwestern.evolution.genotypes.RealValuedGenotype;
import edu.southwestern.util.random.RandomNumbers;

/**
 * This class implements a polynomial mutation operator.
 * 
 * I based my implementation on code from jMetal, which is here:
 * https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/operator/mutation/impl/PolynomialMutation.java
 * Their implementation is based on NSGA-II code available in
 * http://www.iitk.ac.in/kangal/codes.shtml
 * 
 * The mutation operation is described in this paper:
 * https://www.iitk.ac.in/kangal/papers/k2012016.pdf
 * However, the connection between those formulas and the ones in the
 * code below are confusing.
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
