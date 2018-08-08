package edu.southwestern.evolution.genotypes;

import edu.southwestern.evolution.mutation.real.PerturbMutation;
import edu.southwestern.evolution.mutation.real.PolynomialMutation;
import edu.southwestern.util.random.RandomNumbers;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.util.datastructures.ArrayUtil;

import java.util.ArrayList;

/**
 * Genotype that is a fixed-length sequence of real numbers, all of which are
 * confined to specified ranges unique to each position in the genotype.
 * 
 * @author Jacob Schrum
 */
public class BoundedRealValuedGenotype extends RealValuedGenotype {

	double[] lower; // Lowest allowable value for each gene position
	double[] upper; // Highest allowable value for each gene position
	boolean polynomialMutation; // Whether or not polynomial mutation should be
								// used

	public BoundedRealValuedGenotype() {
		// May need to change this if other tasks start using the real-coded
		// genotype
		this(MMNEAT.getLowerBounds(), MMNEAT.getUpperBounds());
	}

	public BoundedRealValuedGenotype(double[] lower, double[] upper) {
		this(RandomNumbers.randomBoundedArray(lower, upper), lower, upper);
	}

	private BoundedRealValuedGenotype(double[] genes, double[] lower, double[] upper) {
		super(genes);
		// Specialized mutation operator slightly more complicated than simple
		// perturbation
		polynomialMutation = Parameters.parameters.booleanParameter("polynomialMutation");

		this.lower = lower;
		this.upper = upper;
		bound();
	}

	public BoundedRealValuedGenotype(ArrayList<Double> genes, double[] lower, double[] upper) {
		this(ArrayUtil.doubleArrayFromList(genes), lower, upper);
	}

	@SuppressWarnings("unused")
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
		if (polynomialMutation) { // Specialized mutation operator slightly more
									// complicated than simple perturbation
			new PolynomialMutation().mutate(this);
		} else { // Default
			new PerturbMutation(getRange()).mutate(this);
		}

		bound();
	}

	/**
	 * Push gene values that are out of bounds back to the particular bound they
	 * crossed.
	 */
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
